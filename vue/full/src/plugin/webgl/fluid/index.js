'use strict'
import {
    createDoubleFBO,
    createFBO,
    downloadURI,
    framebufferToTexture,
    generateColor,
    getResolution,
    getWebGLContext,
    isMobile,
    normalizeColor,
    normalizeTexture,
    resizeCanvas,
    scaleByPixelRatio,
    textureToCanvas,
    updatePointerDownData,
    updatePointerMoveData,
    updatePointerUpData,
    wrap
} from "./util"
import factory from './factory'
import config from './config'
import Pointer from './Pointer'

const WebGLFluid = canvas => {
    resizeCanvas(canvas)

    let pointers = []
    let splatStack = []
    let bloomFrameBuffers = []
    let lastUpdateTime = Date.now()
    let colorUpdateTimer = 0.0
    pointers.push(new Pointer())

    const {gl, ext} = getWebGLContext(canvas)

    if (isMobile()) {
        config.DYE_RESOLUTION = 512
    }
    if (!ext.supportLinearFiltering) {
        config.DYE_RESOLUTION = 512
        config.SHADING = false
        config.BLOOM = false
        config.SUNRAYS = false
    }

    let dye
    let velocity
    let divergence
    let curl
    let pressure
    let bloom
    let sunrays
    let sunraysTemp

    const blit = (() => {
        gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer())
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([-1, -1, -1, 1, 1, 1, 1, -1]), gl.STATIC_DRAW)
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, gl.createBuffer())
        gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array([0, 1, 2, 0, 2, 3]), gl.STATIC_DRAW)
        gl.vertexAttribPointer(0, 2, gl.FLOAT, false, 0, 0)
        gl.enableVertexAttribArray(0)

        return destination => {
            gl.bindFramebuffer(gl.FRAMEBUFFER, destination)
            gl.drawElements(gl.TRIANGLES, 6, gl.UNSIGNED_SHORT, 0)
        }
    })()

    const {
        blurProgram, copyProgram, clearProgram, colorProgram, checkerboardProgram, bloomPrefilterProgram,
        bloomBlurProgram, bloomFinalProgram, sunraysMaskProgram, sunraysProgram, splatProgram, advectionProgram,
        divergenceProgram, curlProgram, vorticityProgram, pressureProgram, gradienSubtractProgram, displayMaterial
    } = factory(gl, ext)

    function startGUI() {
        if (isMobile()) return
        let gui = new dat.GUI({width: 300})
        gui
            .add(config, 'DYE_RESOLUTION', {high: 1024, medium: 512, low: 256, 'very low': 128})
            .name('quality')
            .onFinishChange(initFrameBuffers())
        gui
            .add(config, 'SIM_RESOLUTION', {'32': 32, '64': 64, '128': 128, '256': 256})
            .name('sim resolution')
            .onFinishChange(initFrameBuffers())
        gui.add(config, 'DENSITY_DISSIPATION', 0, 4.0).name('density diffusion')
        gui.add(config, 'VELOCITY_DISSIPATION', 0, 4.0).name('velocity diffusion')
        gui.add(config, 'PRESSURE', 0.0, 1.0).name('pressure')
        gui
            .add(config, 'CURL', 0, 50)
            .name('vorticity')
            .step(1)
        gui.add(config, 'SPLAT_RADIUS', 0.01, 1.0).name('splat radius')
        gui
            .add(config, 'SHADING')
            .name('shading')
            .onFinishChange(updateKeywords)
        gui.add(config, 'COLORFUL').name('colorful')
        gui
            .add(config, 'PAUSED')
            .name('paused')
            .listen()
        gui
            .add({fun: () => splatStack.push(parseInt(Math.random() * 20) + 5)}, 'fun')
            .name('Random splats')

        let bloomFolder = gui.addFolder('Bloom')
        bloomFolder
            .add(config, 'BLOOM')
            .name('enabled')
            .onFinishChange(updateKeywords)
        bloomFolder.add(config, 'BLOOM_INTENSITY', 0.1, 2.0).name('intensity')
        bloomFolder.add(config, 'BLOOM_THRESHOLD', 0.0, 1.0).name('threshold')

        let sunraysFolder = gui.addFolder('Sunrays')
        sunraysFolder
            .add(config, 'SUNRAYS')
            .name('enabled')
            .onFinishChange(updateKeywords)
        sunraysFolder.add(config, 'SUNRAYS_WEIGHT', 0.3, 1.0).name('weight')

        let captureFolder = gui.addFolder('Capture')
        captureFolder.addColor(config, 'BACK_COLOR').name('background color')
        captureFolder.add(config, 'TRANSPARENT').name('transparent')
        captureFolder.add({fun: captureScreenShot}, 'fun').name('take screenshot')
    }

    function initFrameBuffers() {
        let simRes = getResolution(gl, config.SIM_RESOLUTION)
        let dyeRes = getResolution(gl, config.DYE_RESOLUTION)

        const texType = ext.halfFloatTexType
        const rgba = ext.formatRGBA
        const rg = ext.formatRG
        const r = ext.formatR
        const filtering = ext.supportLinearFiltering ? gl.LINEAR : gl.NEAREST

        if (dye == null) dye = createDoubleFBO(gl, dyeRes.width, dyeRes.height, rgba.internalFormat, rgba.format, texType, filtering)
        else dye = resizeDoubleFBO(dye, dyeRes.width, dyeRes.height, rgba.internalFormat, rgba.format, texType, filtering)

        if (velocity == null) velocity = createDoubleFBO(gl, simRes.width, simRes.height, rg.internalFormat, rg.format, texType, filtering)
        else velocity = resizeDoubleFBO(velocity, simRes.width, simRes.height, rg.internalFormat, rg.format, texType, filtering)

        divergence = createFBO(gl, simRes.width, simRes.height, r.internalFormat, r.format, texType, gl.NEAREST)
        curl = createFBO(gl, simRes.width, simRes.height, r.internalFormat, r.format, texType, gl.NEAREST)
        pressure = createDoubleFBO(gl, simRes.width, simRes.height, r.internalFormat, r.format, texType, gl.NEAREST)

        initBloomFrameBuffers()
        initSunraysFrameBuffers()
    }

    function initBloomFrameBuffers() {
        let res = getResolution(gl, config.BLOOM_RESOLUTION)

        const texType = ext.halfFloatTexType
        const rgba = ext.formatRGBA
        const filtering = ext.supportLinearFiltering ? gl.LINEAR : gl.NEAREST

        bloom = createFBO(gl, res.width, res.height, rgba.internalFormat, rgba.format, texType, filtering)

        bloomFrameBuffers.length = 0
        for (let i = 0; i < config.BLOOM_ITERATIONS; i++) {
            let width = res.width >> (i + 1)
            let height = res.height >> (i + 1)

            if (width < 2 || height < 2) break

            let fbo = createFBO(gl, width, height, rgba.internalFormat, rgba.format, texType, filtering)
            bloomFrameBuffers.push(fbo)
        }
    }

    function initSunraysFrameBuffers() {
        let res = getResolution(gl, config.SUNRAYS_RESOLUTION)

        const texType = ext.halfFloatTexType
        const r = ext.formatR
        const filtering = ext.supportLinearFiltering ? gl.LINEAR : gl.NEAREST

        sunrays = createFBO(gl, res.width, res.height, r.internalFormat, r.format, texType, filtering)
        sunraysTemp = createFBO(gl, res.width, res.height, r.internalFormat, r.format, texType, filtering)
    }

    function resizeFBO(target, w, h, internalFormat, format, type, param) {
        let newFBO = createFBO(gl, w, h, internalFormat, format, type, param)
        copyProgram.bind(gl)
        gl.uniform1i(copyProgram.uniforms.uTexture, target.attach(0))
        blit(newFBO.fbo)
        return newFBO
    }

    function resizeDoubleFBO(target, w, h, internalFormat, format, type, param) {
        if (target.width === w && target.height === h) return target
        target.read = resizeFBO(target.read, w, h, internalFormat, format, type, param)
        target.write = createFBO(gl, w, h, internalFormat, format, type, param)
        target.width = w
        target.height = h
        target.texelSizeX = 1.0 / w
        target.texelSizeY = 1.0 / h
        return target
    }

    function updateKeywords() {
        let displayKeywords = []
        if (config.SHADING) displayKeywords.push('SHADING')
        if (config.BLOOM) displayKeywords.push('BLOOM')
        if (config.SUNRAYS) displayKeywords.push('SUNRAYS')
        displayMaterial.setKeywords(gl, displayKeywords)
    }

    function update() {
        const dt = calcDeltaTime()
        if (resizeCanvas(canvas)) initFrameBuffers()
        updateColors(dt)
        applyInputs()
        if (!config.PAUSED) step(dt)
        render(null)
        window.requestAnimationFrame(update)
    }

    function calcDeltaTime() {
        let now = Date.now()
        let dt = (now - lastUpdateTime) / 1000
        dt = Math.min(dt, 0.016666)
        lastUpdateTime = now
        return dt
    }

    function updateColors(dt) {
        if (!config.COLORFUL) return

        colorUpdateTimer += dt * config.COLOR_UPDATE_SPEED
        if (colorUpdateTimer >= 1) {
            colorUpdateTimer = wrap(colorUpdateTimer, 0, 1)
            pointers.forEach(p => {
                p.color = generateColor()
            })
        }
    }

    function applyInputs() {
        if (splatStack.length > 0) multipleSplats(splatStack.pop())

        pointers.forEach(p => {
            if (p.moved) {
                p.moved = false
                splatPointer(p)
            }
        })
    }

    function step(dt) {
        gl.disable(gl.BLEND)
        gl.viewport(0, 0, velocity.width, velocity.height)

        curlProgram.bind(gl)
        gl.uniform2f(curlProgram.uniforms.texelSize, velocity.texelSizeX, velocity.texelSizeY)
        gl.uniform1i(curlProgram.uniforms.uVelocity, velocity.read.attach(0))
        blit(curl.fbo)

        vorticityProgram.bind(gl)
        gl.uniform2f(vorticityProgram.uniforms.texelSize, velocity.texelSizeX, velocity.texelSizeY)
        gl.uniform1i(vorticityProgram.uniforms.uVelocity, velocity.read.attach(0))
        gl.uniform1i(vorticityProgram.uniforms.uCurl, curl.attach(1))
        gl.uniform1f(vorticityProgram.uniforms.curl, config.CURL)
        gl.uniform1f(vorticityProgram.uniforms.dt, dt)
        blit(velocity.write.fbo)
        velocity.swap()

        divergenceProgram.bind(gl)
        gl.uniform2f(divergenceProgram.uniforms.texelSize, velocity.texelSizeX, velocity.texelSizeY)
        gl.uniform1i(divergenceProgram.uniforms.uVelocity, velocity.read.attach(0))
        blit(divergence.fbo)

        clearProgram.bind(gl)
        gl.uniform1i(clearProgram.uniforms.uTexture, pressure.read.attach(0))
        gl.uniform1f(clearProgram.uniforms.value, config.PRESSURE)
        blit(pressure.write.fbo)
        pressure.swap()

        pressureProgram.bind(gl)
        gl.uniform2f(pressureProgram.uniforms.texelSize, velocity.texelSizeX, velocity.texelSizeY)
        gl.uniform1i(pressureProgram.uniforms.uDivergence, divergence.attach(0))
        for (let i = 0; i < config.PRESSURE_ITERATIONS; i++) {
            gl.uniform1i(pressureProgram.uniforms.uPressure, pressure.read.attach(1))
            blit(pressure.write.fbo)
            pressure.swap()
        }

        gradienSubtractProgram.bind(gl)
        gl.uniform2f(gradienSubtractProgram.uniforms.texelSize, velocity.texelSizeX, velocity.texelSizeY)
        gl.uniform1i(gradienSubtractProgram.uniforms.uPressure, pressure.read.attach(0))
        gl.uniform1i(gradienSubtractProgram.uniforms.uVelocity, velocity.read.attach(1))
        blit(velocity.write.fbo)
        velocity.swap()

        advectionProgram.bind(gl)
        gl.uniform2f(advectionProgram.uniforms.texelSize, velocity.texelSizeX, velocity.texelSizeY)
        if (!ext.supportLinearFiltering) gl.uniform2f(advectionProgram.uniforms.dyeTexelSize, velocity.texelSizeX, velocity.texelSizeY)
        let velocityId = velocity.read.attach(0)
        gl.uniform1i(advectionProgram.uniforms.uVelocity, velocityId)
        gl.uniform1i(advectionProgram.uniforms.uSource, velocityId)
        gl.uniform1f(advectionProgram.uniforms.dt, dt)
        gl.uniform1f(advectionProgram.uniforms.dissipation, config.VELOCITY_DISSIPATION)
        blit(velocity.write.fbo)
        velocity.swap()

        gl.viewport(0, 0, dye.width, dye.height)

        if (!ext.supportLinearFiltering) gl.uniform2f(advectionProgram.uniforms.dyeTexelSize, dye.texelSizeX, dye.texelSizeY)
        gl.uniform1i(advectionProgram.uniforms.uVelocity, velocity.read.attach(0))
        gl.uniform1i(advectionProgram.uniforms.uSource, dye.read.attach(1))
        gl.uniform1f(advectionProgram.uniforms.dissipation, config.DENSITY_DISSIPATION)
        blit(dye.write.fbo)
        dye.swap()
    }

    function render(target) {
        if (config.BLOOM) {
            applyBloom(dye.read, bloom)
        }
        if (config.SUNRAYS) {
            applySunrays(dye.read, dye.write, sunrays)
            blur(sunrays, sunraysTemp, 1)
        }

        if (target == null || !config.TRANSPARENT) {
            gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA)
            gl.enable(gl.BLEND)
        }
        else {
            gl.disable(gl.BLEND)
        }

        let width = target == null ? gl.drawingBufferWidth : target.width
        let height = target == null ? gl.drawingBufferHeight : target.height
        gl.viewport(0, 0, width, height)

        let fbo = target == null ? null : target.fbo
        if (!config.TRANSPARENT) drawColor(fbo, normalizeColor(config.BACK_COLOR))
        if (target == null && config.TRANSPARENT) drawCheckerboard(fbo)
        drawDisplay(fbo, width, height)
    }

    function drawColor(fbo, color) {
        colorProgram.bind(gl)
        gl.uniform4f(colorProgram.uniforms.color, color.r, color.g, color.b, 1)
        blit(fbo)
    }

    function drawCheckerboard(fbo) {
        checkerboardProgram.bind(gl)
        gl.uniform1f(checkerboardProgram.uniforms.aspectRatio, canvas.width / canvas.height)
        blit(fbo)
    }

    function drawDisplay(fbo, width, height) {
        displayMaterial.bind(gl)
        if (config.SHADING) gl.uniform2f(displayMaterial.uniforms.texelSize, 1.0 / width, 1.0 / height)
        gl.uniform1i(displayMaterial.uniforms.uTexture, dye.read.attach(0))
        if (config.BLOOM) {
            gl.uniform1i(displayMaterial.uniforms.uBloom, bloom.attach(1))
        }
        if (config.SUNRAYS) gl.uniform1i(displayMaterial.uniforms.uSunrays, sunrays.attach(3))
        blit(fbo)
    }

    function applyBloom(source, destination) {
        if (bloomFrameBuffers.length < 2) return

        let last = destination

        gl.disable(gl.BLEND)
        bloomPrefilterProgram.bind(gl)
        let knee = config.BLOOM_THRESHOLD * config.BLOOM_SOFT_KNEE + 0.0001
        let curve0 = config.BLOOM_THRESHOLD - knee
        let curve1 = knee * 2
        let curve2 = 0.25 / knee
        gl.uniform3f(bloomPrefilterProgram.uniforms.curve, curve0, curve1, curve2)
        gl.uniform1f(bloomPrefilterProgram.uniforms.threshold, config.BLOOM_THRESHOLD)
        gl.uniform1i(bloomPrefilterProgram.uniforms.uTexture, source.attach(0))
        gl.viewport(0, 0, last.width, last.height)
        blit(last.fbo)

        bloomBlurProgram.bind(gl)
        for (let i = 0; i < bloomFrameBuffers.length; i++) {
            let dest = bloomFrameBuffers[i]
            gl.uniform2f(bloomBlurProgram.uniforms.texelSize, last.texelSizeX, last.texelSizeY)
            gl.uniform1i(bloomBlurProgram.uniforms.uTexture, last.attach(0))
            gl.viewport(0, 0, dest.width, dest.height)
            blit(dest.fbo)
            last = dest
        }

        gl.blendFunc(gl.ONE, gl.ONE)
        gl.enable(gl.BLEND)

        for (let i = bloomFrameBuffers.length - 2; i >= 0; i--) {
            let baseTex = bloomFrameBuffers[i]
            gl.uniform2f(bloomBlurProgram.uniforms.texelSize, last.texelSizeX, last.texelSizeY)
            gl.uniform1i(bloomBlurProgram.uniforms.uTexture, last.attach(0))
            gl.viewport(0, 0, baseTex.width, baseTex.height)
            blit(baseTex.fbo)
            last = baseTex
        }

        gl.disable(gl.BLEND)
        bloomFinalProgram.bind(gl)
        gl.uniform2f(bloomFinalProgram.uniforms.texelSize, last.texelSizeX, last.texelSizeY)
        gl.uniform1i(bloomFinalProgram.uniforms.uTexture, last.attach(0))
        gl.uniform1f(bloomFinalProgram.uniforms.intensity, config.BLOOM_INTENSITY)
        gl.viewport(0, 0, destination.width, destination.height)
        blit(destination.fbo)
    }

    function applySunrays(source, mask, destination) {
        gl.disable(gl.BLEND)
        sunraysMaskProgram.bind(gl)
        gl.uniform1i(sunraysMaskProgram.uniforms.uTexture, source.attach(0))
        gl.viewport(0, 0, mask.width, mask.height)
        blit(mask.fbo)

        sunraysProgram.bind(gl)
        gl.uniform1f(sunraysProgram.uniforms.weight, config.SUNRAYS_WEIGHT)
        gl.uniform1i(sunraysProgram.uniforms.uTexture, mask.attach(0))
        gl.viewport(0, 0, destination.width, destination.height)
        blit(destination.fbo)
    }

    function blur(target, temp, iterations) {
        blurProgram.bind(gl)
        for (let i = 0; i < iterations; i++) {
            gl.uniform2f(blurProgram.uniforms.texelSize, target.texelSizeX, 0.0)
            gl.uniform1i(blurProgram.uniforms.uTexture, target.attach(0))
            blit(temp.fbo)

            gl.uniform2f(blurProgram.uniforms.texelSize, 0.0, target.texelSizeY)
            gl.uniform1i(blurProgram.uniforms.uTexture, temp.attach(0))
            blit(target.fbo)
        }
    }

    function splatPointer(pointer) {
        let dx = pointer.deltaX * config.SPLAT_FORCE
        let dy = pointer.deltaY * config.SPLAT_FORCE
        splat(pointer.texcoordX, pointer.texcoordY, dx, dy, pointer.color)
    }

    function multipleSplats(amount) {
        for (let i = 0; i < amount; i++) {
            const color = generateColor()
            color.r *= 10.0
            color.g *= 10.0
            color.b *= 10.0
            const x = Math.random()
            const y = Math.random()
            const dx = 1000 * (Math.random() - 0.5)
            const dy = 1000 * (Math.random() - 0.5)
            splat(x, y, dx, dy, color)
        }
    }

    function splat(x, y, dx, dy, color) {
        gl.viewport(0, 0, velocity.width, velocity.height)
        splatProgram.bind(gl)
        gl.uniform1i(splatProgram.uniforms.uTarget, velocity.read.attach(0))
        gl.uniform1f(splatProgram.uniforms.aspectRatio, canvas.width / canvas.height)
        gl.uniform2f(splatProgram.uniforms.point, x, y)
        gl.uniform3f(splatProgram.uniforms.color, dx, dy, 0.0)
        gl.uniform1f(splatProgram.uniforms.radius, correctRadius(config.SPLAT_RADIUS / 100.0))
        blit(velocity.write.fbo)
        velocity.swap()

        gl.viewport(0, 0, dye.width, dye.height)
        gl.uniform1i(splatProgram.uniforms.uTarget, dye.read.attach(0))
        gl.uniform3f(splatProgram.uniforms.color, color.r, color.g, color.b)
        blit(dye.write.fbo)
        dye.swap()
    }

    function correctRadius(radius) {
        let aspectRatio = canvas.width / canvas.height
        if (aspectRatio > 1) radius *= aspectRatio
        return radius
    }

    function captureScreenShot() {
        let res = getResolution(gl, config.CAPTURE_RESOLUTION)
        let target = createFBO(gl, res.width, res.height, ext.formatRGBA.internalFormat, ext.formatRGBA.format, ext.halfFloatTexType, gl.NEAREST)
        render(target)

        let texture = framebufferToTexture(gl, target)
        texture = normalizeTexture(texture, target.width, target.height)

        let captureCanvas = textureToCanvas(texture, target.width, target.height)
        let dataUri = captureCanvas.toDataURL()
        downloadURI('fluid.png', dataUri)
        window.URL.revokeObjectURL(dataUri)
    }

    //startGUI();
    updateKeywords()
    initFrameBuffers()
    multipleSplats(parseInt(Math.random() * 20) + 5)
    update()

    canvas.addEventListener('mousedown', e => {
        let posX = scaleByPixelRatio(e.offsetX)
        let posY = scaleByPixelRatio(e.offsetY)
        let pointer = pointers.find(p => p.id === -1)
        if (pointer == null) pointer = new Pointer()
        updatePointerDownData(canvas, pointer, -1, posX, posY)
    })

    canvas.addEventListener('mousemove', e => {
        let posX = scaleByPixelRatio(e.offsetX)
        let posY = scaleByPixelRatio(e.offsetY)
        updatePointerMoveData(canvas, pointers[0], posX, posY)
    })

    canvas.addEventListener('touchstart', e => {
        e.preventDefault()
        const touches = e.targetTouches
        while (touches.length >= pointers.length) pointers.push(new Pointer())
        for (let i = 0; i < touches.length; i++) {
            let posX = scaleByPixelRatio(touches[i].pageX)
            let posY = scaleByPixelRatio(touches[i].pageY)
            updatePointerDownData(canvas, pointers[i + 1], touches[i].identifier, posX, posY)
        }
    })

    canvas.addEventListener('touchmove', e => {
        e.preventDefault()
        const touches = e.targetTouches
        for (let i = 0; i < touches.length; i++) {
            let posX = scaleByPixelRatio(touches[i].pageX)
            let posY = scaleByPixelRatio(touches[i].pageY)
            updatePointerMoveData(canvas, pointers[i + 1], posX, posY)
        }
    })

    window.addEventListener('mouseup', () => {
        updatePointerUpData(pointers[0])
    })

    window.addEventListener('touchend', e => {
        const touches = e.changedTouches
        for (let i = 0; i < touches.length; i++) {
            let pointer = pointers.find(p => p.id === touches[i].identifier)
            updatePointerUpData(pointer)
        }
    })

    window.addEventListener('keydown', e => {
        if (e.code === 'KeyP') config.PAUSED = !config.PAUSED
        if (e.key === ' ') splatStack.push(parseInt(Math.random() * 20) + 5)
        if (e.key === 's') {
            captureScreenShot()
        }
    })
}

export default WebGLFluid
