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

export default class WebGlFluid {
    constructor(canvas) {
        this.pointers = [new Pointer()]
        this.splatStack = []
        this.bloomFrameBuffers = []
        this.lastUpdateTime = Date.now()
        this.colorUpdateTimer = 0.0

        this.canvas = canvas
        const {gl, ext} = getWebGLContext(canvas)
        this.gl = gl
        this.ext = ext

        if (isMobile()) {
            config.DYE_RESOLUTION = 512
        }
        if (!ext.supportLinearFiltering) {
            config.DYE_RESOLUTION = 512
            config.SHADING = false
            config.BLOOM = false
            config.SUNRAYS = false
        }

        this.blit = (() => {
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

        this.factory = factory(gl, ext)

        this.rAF = null
        this.stopSign = false

        this.loop = this.loop.bind(this)
        this.onMousedown = this.onMousedown.bind(this)
        this.onMousemove = this.onMousemove.bind(this)
        this.onMouseup = this.onMouseup.bind(this)
        this.onTouchstart = this.onTouchstart.bind(this)
        this.onTouchmove = this.onTouchmove.bind(this)
        this.onTouchend = this.onTouchend.bind(this)
        this.onKeydown = this.onKeydown.bind(this)

        this.start()
    }

    start() {
        this.resize()
        this.updateKeywords()
        this.initFrameBuffers()
        this.multipleSplats(~~(Math.random() * 20) + 5)
        this.loop()

        this.canvas.addEventListener('mousedown', this.onMousedown)
        this.canvas.addEventListener('mousemove', this.onMousemove)
        window.addEventListener('mouseup', this.onMouseup)

        this.canvas.addEventListener('touchstart', this.onTouchstart)
        this.canvas.addEventListener('touchmove', this.onTouchmove)
        window.addEventListener('touchend', this.onTouchend)

        window.addEventListener('keydown', this.onKeydown)
    }

    stop() {
        this.canvas.removeAllListeners()
        window.removeListener('mouseup', this.onMouseup)
        window.removeListener('touchend', this.onTouchend)
        window.removeListener('keydown', this.onKeydown)
        this.stopSign = true
        this.gl.getExtension('WEBGL_lose_context').loseContext()
    }

    loop() {
        if (this.stopSign) {
            this.rAF && window.cancelAnimationFrame(this.rAF)
            return
        }
        const dt = this.calcDeltaTime()
        if (this.resize()) this.initFrameBuffers()
        this.updateColors(dt)
        this.applyInputs()
        if (!config.PAUSED) this.step(dt)
        this.render(null)
        this.rAF = window.requestAnimationFrame(this.loop)
    }

    resize() {
        return resizeCanvas(this.canvas)
    }

    onMousedown(e) {
        const posX = scaleByPixelRatio(e.offsetX)
        const posY = scaleByPixelRatio(e.offsetY)
        let pointer = this.pointers.find(p => p.id === -1)
        if (!pointer) pointer = new Pointer()
        updatePointerDownData(this.canvas, pointer, -1, posX, posY)
    }

    onMousemove(e) {
        const posX = scaleByPixelRatio(e.offsetX)
        const posY = scaleByPixelRatio(e.offsetY)
        updatePointerMoveData(this.canvas, this.pointers[0], posX, posY)
    }

    onMouseup() {
        updatePointerUpData(this.pointers[0])
    }

    onTouchstart(e) {
        e.preventDefault()
        const touches = e.targetTouches
        while (touches.length >= this.pointers.length) this.pointers.push(new Pointer())
        for (let i = 0; i < touches.length; i++) {
            let posX = scaleByPixelRatio(touches[i].pageX)
            let posY = scaleByPixelRatio(touches[i].pageY)
            updatePointerDownData(this.canvas, this.pointers[i + 1], touches[i].identifier, posX, posY)
        }
    }

    onTouchmove(e) {
        e.preventDefault()
        const touches = e.targetTouches
        for (let i = 0; i < touches.length; i++) {
            const posX = scaleByPixelRatio(touches[i].pageX)
            const posY = scaleByPixelRatio(touches[i].pageY)
            updatePointerMoveData(this.canvas, this.pointers[i + 1], posX, posY)
        }
    }

    onTouchend(e) {
        for (const {identifier} of e.changedTouches) {
            const pointer = this.pointers.find(p => p.id === identifier)
            updatePointerUpData(pointer)
        }
    }

    onKeydown(e) {
        if (e.code === 'KeyP') config.PAUSED = !config.PAUSED
        if (e.key === ' ') this.splatStack.push(~~(Math.random() * 20) + 5)
        if (e.key === 's') this.captureScreenShot()
    }

    initFrameBuffers() {
        const simRes = getResolution(this.gl, config.SIM_RESOLUTION)
        const dyeRes = getResolution(this.gl, config.DYE_RESOLUTION)

        const texType = this.ext.halfFloatTexType
        const rgba = this.ext.formatRGBA
        const rg = this.ext.formatRG
        const r = this.ext.formatR
        const filtering = this.ext.supportLinearFiltering ? this.gl.LINEAR : this.gl.NEAREST

        if (this.dye == null) this.dye = createDoubleFBO(this.gl, dyeRes.width, dyeRes.height, rgba.internalFormat, rgba.format, texType, filtering)
        else this.dye = this.resizeDoubleFBO(this.dye, dyeRes.width, dyeRes.height, rgba.internalFormat, rgba.format, texType, filtering)

        if (this.velocity == null) this.velocity = createDoubleFBO(this.gl, simRes.width, simRes.height, rg.internalFormat, rg.format, texType, filtering)
        else this.velocity = this.resizeDoubleFBO(this.velocity, simRes.width, simRes.height, rg.internalFormat, rg.format, texType, filtering)

        this.divergence = createFBO(this.gl, simRes.width, simRes.height, r.internalFormat, r.format, texType, this.gl.NEAREST)
        this.curl = createFBO(this.gl, simRes.width, simRes.height, r.internalFormat, r.format, texType, this.gl.NEAREST)
        this.pressure = createDoubleFBO(this.gl, simRes.width, simRes.height, r.internalFormat, r.format, texType, this.gl.NEAREST)

        this.initBloomFrameBuffers()
        this.initSunraysFrameBuffers()
    }

    initBloomFrameBuffers() {
        let res = getResolution(this.gl, config.BLOOM_RESOLUTION)

        const texType = this.ext.halfFloatTexType
        const rgba = this.ext.formatRGBA
        const filtering = this.ext.supportLinearFiltering ? this.gl.LINEAR : this.gl.NEAREST

        this.bloom = createFBO(this.gl, res.width, res.height, rgba.internalFormat, rgba.format, texType, filtering)

        this.bloomFrameBuffers.length = 0
        for (let i = 0; i < config.BLOOM_ITERATIONS; i++) {
            let width = res.width >> (i + 1)
            let height = res.height >> (i + 1)

            if (width < 2 || height < 2) break

            let fbo = createFBO(this.gl, width, height, rgba.internalFormat, rgba.format, texType, filtering)
            this.bloomFrameBuffers.push(fbo)
        }
    }

    initSunraysFrameBuffers() {
        let res = getResolution(this.gl, config.SUNRAYS_RESOLUTION)

        const texType = this.ext.halfFloatTexType
        const r = this.ext.formatR
        const filtering = this.ext.supportLinearFiltering ? this.gl.LINEAR : this.gl.NEAREST

        this.sunrays = createFBO(this.gl, res.width, res.height, r.internalFormat, r.format, texType, filtering)
        this.sunraysTemp = createFBO(this.gl, res.width, res.height, r.internalFormat, r.format, texType, filtering)
    }

    resizeFBO(target, w, h, internalFormat, format, type, param) {
        let newFBO = createFBO(this.gl, w, h, internalFormat, format, type, param)
        this.factory.copyProgram.bind(this.gl)
        this.gl.uniform1i(this.factory.copyProgram.uniforms.uTexture, target.attach(0))
        this.blit(newFBO.fbo)
        return newFBO
    }

    resizeDoubleFBO(target, w, h, internalFormat, format, type, param) {
        if (target.width === w && target.height === h) return target
        target.read = this.resizeFBO(target.read, w, h, internalFormat, format, type, param)
        target.write = createFBO(this.gl, w, h, internalFormat, format, type, param)
        target.width = w
        target.height = h
        target.texelSizeX = 1.0 / w
        target.texelSizeY = 1.0 / h
        return target
    }

    updateKeywords() {
        const displayKeywords = []
        config.SHADING && displayKeywords.push('SHADING')
        config.BLOOM && displayKeywords.push('BLOOM')
        config.SUNRAYS && displayKeywords.push('SUNRAYS')
        this.factory.displayMaterial.setKeywords(this.gl, displayKeywords)
    }

    calcDeltaTime() {
        const now = Date.now()
        this.lastUpdateTime = now
        return Math.min((now - this.lastUpdateTime) / 1000, 0.016666)
    }

    updateColors(dt) {
        if (!config.COLORFUL) return

        this.colorUpdateTimer += dt * config.COLOR_UPDATE_SPEED
        if (this.colorUpdateTimer >= 1) {
            this.colorUpdateTimer = wrap(this.colorUpdateTimer, 0, 1)
            this.pointers.forEach(p => p.color = generateColor())
        }
    }

    applyInputs() {
        if (this.splatStack.length > 0) this.multipleSplats(this.splatStack.pop())

        this.pointers.forEach(p => {
            if (p.moved) {
                p.moved = false
                this.splatPointer(p)
            }
        })
    }

    step(dt) {
        const gl = this.gl

        gl.disable(gl.BLEND)
        gl.viewport(0, 0, this.velocity.width, this.velocity.height)

        this.factory.curlProgram.bind(gl)
        gl.uniform2f(this.factory.curlProgram.uniforms.texelSize, this.velocity.texelSizeX, this.velocity.texelSizeY)
        gl.uniform1i(this.factory.curlProgram.uniforms.uVelocity, this.velocity.read.attach(0))
        this.blit(this.curl.fbo)

        this.factory.vorticityProgram.bind(gl)
        gl.uniform2f(this.factory.vorticityProgram.uniforms.texelSize, this.velocity.texelSizeX, this.velocity.texelSizeY)
        gl.uniform1i(this.factory.vorticityProgram.uniforms.uVelocity, this.velocity.read.attach(0))
        gl.uniform1i(this.factory.vorticityProgram.uniforms.uCurl, this.curl.attach(1))
        gl.uniform1f(this.factory.vorticityProgram.uniforms.curl, config.CURL)
        gl.uniform1f(this.factory.vorticityProgram.uniforms.dt, dt)
        this.blit(this.velocity.write.fbo)
        this.velocity.swap()

        this.factory.divergenceProgram.bind(gl)
        gl.uniform2f(this.factory.divergenceProgram.uniforms.texelSize, this.velocity.texelSizeX, this.velocity.texelSizeY)
        gl.uniform1i(this.factory.divergenceProgram.uniforms.uVelocity, this.velocity.read.attach(0))
        this.blit(this.divergence.fbo)

        this.factory.clearProgram.bind(gl)
        gl.uniform1i(this.factory.clearProgram.uniforms.uTexture, this.pressure.read.attach(0))
        gl.uniform1f(this.factory.clearProgram.uniforms.value, config.PRESSURE)
        this.blit(this.pressure.write.fbo)
        this.pressure.swap()

        this.factory.pressureProgram.bind(gl)
        gl.uniform2f(this.factory.pressureProgram.uniforms.texelSize, this.velocity.texelSizeX, this.velocity.texelSizeY)
        gl.uniform1i(this.factory.pressureProgram.uniforms.uDivergence, this.divergence.attach(0))
        for (let i = 0; i < config.PRESSURE_ITERATIONS; i++) {
            gl.uniform1i(this.factory.pressureProgram.uniforms.uPressure, this.pressure.read.attach(1))
            this.blit(this.pressure.write.fbo)
            this.pressure.swap()
        }

        this.factory.gradienSubtractProgram.bind(gl)
        gl.uniform2f(this.factory.gradienSubtractProgram.uniforms.texelSize, this.velocity.texelSizeX, this.velocity.texelSizeY)
        gl.uniform1i(this.factory.gradienSubtractProgram.uniforms.uPressure, this.pressure.read.attach(0))
        gl.uniform1i(this.factory.gradienSubtractProgram.uniforms.uVelocity, this.velocity.read.attach(1))
        this.blit(this.velocity.write.fbo)
        this.velocity.swap()

        this.factory.advectionProgram.bind(gl)
        gl.uniform2f(this.factory.advectionProgram.uniforms.texelSize, this.velocity.texelSizeX, this.velocity.texelSizeY)
        if (!this.ext.supportLinearFiltering) {
            gl.uniform2f(this.factory.advectionProgram.uniforms.dyeTexelSize, this.velocity.texelSizeX, this.velocity.texelSizeY)
        }
        let velocityId = this.velocity.read.attach(0)
        gl.uniform1i(this.factory.advectionProgram.uniforms.uVelocity, velocityId)
        gl.uniform1i(this.factory.advectionProgram.uniforms.uSource, velocityId)
        gl.uniform1f(this.factory.advectionProgram.uniforms.dt, dt)
        gl.uniform1f(this.factory.advectionProgram.uniforms.dissipation, config.VELOCITY_DISSIPATION)
        this.blit(this.velocity.write.fbo)
        this.velocity.swap()

        gl.viewport(0, 0, this.dye.width, this.dye.height)

        if (!this.ext.supportLinearFiltering) {
            gl.uniform2f(this.factory.advectionProgram.uniforms.dyeTexelSize, this.dye.texelSizeX, this.dye.texelSizeY)
        }
        gl.uniform1i(this.factory.advectionProgram.uniforms.uVelocity, this.velocity.read.attach(0))
        gl.uniform1i(this.factory.advectionProgram.uniforms.uSource, this.dye.read.attach(1))
        gl.uniform1f(this.factory.advectionProgram.uniforms.dissipation, config.DENSITY_DISSIPATION)
        this.blit(this.dye.write.fbo)
        this.dye.swap()
    }

    render(target) {
        const gl = this.gl

        if (config.BLOOM) {
            this.applyBloom(this.dye.read, this.bloom)
        }
        if (config.SUNRAYS) {
            this.applySunrays(this.dye.read, this.dye.write, this.sunrays)
            this.blur(this.sunrays, this.sunraysTemp, 1)
        }

        if (target == null || !config.TRANSPARENT) {
            gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA)
            gl.enable(gl.BLEND)
        }
        else gl.disable(gl.BLEND)

        let width = target == null ? gl.drawingBufferWidth : target.width
        let height = target == null ? gl.drawingBufferHeight : target.height
        gl.viewport(0, 0, width, height)

        let fbo = target == null ? null : target.fbo
        if (!config.TRANSPARENT) this.drawColor(fbo, normalizeColor(config.BACK_COLOR))
        if (target == null && config.TRANSPARENT) this.drawCheckerboard(fbo)
        this.drawDisplay(fbo, width, height)
    }

    drawColor(fbo, color) {
        this.factory.colorProgram.bind(this.gl)
        this.gl.uniform4f(this.factory.colorProgram.uniforms.color, color.r, color.g, color.b, 1)
        this.blit(fbo)
    }

    drawCheckerboard(fbo) {
        this.factory.checkerboardProgram.bind(this.gl)
        this.gl.uniform1f(this.factory.checkerboardProgram.uniforms.aspectRatio, this.canvas.width / this.canvas.height)
        this.blit(fbo)
    }

    drawDisplay(fbo, width, height) {
        const gl = this.gl

        this.factory.displayMaterial.bind(gl)
        if (config.SHADING) gl.uniform2f(this.factory.displayMaterial.uniforms.texelSize, 1.0 / width, 1.0 / height)
        gl.uniform1i(this.factory.displayMaterial.uniforms.uTexture, this.dye.read.attach(0))
        if (config.BLOOM) {
            gl.uniform1i(this.factory.displayMaterial.uniforms.uBloom, this.bloom.attach(1))
        }
        if (config.SUNRAYS) gl.uniform1i(this.factory.displayMaterial.uniforms.uSunrays, this.sunrays.attach(3))
        this.blit(fbo)
    }

    applyBloom(source, destination) {
        if (this.bloomFrameBuffers.length < 2) return

        const gl = this.gl

        let last = destination

        gl.disable(gl.BLEND)
        this.factory.bloomPrefilterProgram.bind(gl)
        let knee = config.BLOOM_THRESHOLD * config.BLOOM_SOFT_KNEE + 0.0001
        let curve0 = config.BLOOM_THRESHOLD - knee
        let curve1 = knee * 2
        let curve2 = 0.25 / knee
        gl.uniform3f(this.factory.bloomPrefilterProgram.uniforms.curve, curve0, curve1, curve2)
        gl.uniform1f(this.factory.bloomPrefilterProgram.uniforms.threshold, config.BLOOM_THRESHOLD)
        gl.uniform1i(this.factory.bloomPrefilterProgram.uniforms.uTexture, source.attach(0))
        gl.viewport(0, 0, last.width, last.height)
        this.blit(last.fbo)

        this.factory.bloomBlurProgram.bind(gl)
        for (let i = 0; i < this.bloomFrameBuffers.length; i++) {
            let dest = this.bloomFrameBuffers[i]
            gl.uniform2f(this.factory.bloomBlurProgram.uniforms.texelSize, last.texelSizeX, last.texelSizeY)
            gl.uniform1i(this.factory.bloomBlurProgram.uniforms.uTexture, last.attach(0))
            gl.viewport(0, 0, dest.width, dest.height)
            this.blit(dest.fbo)
            last = dest
        }

        gl.blendFunc(gl.ONE, gl.ONE)
        gl.enable(gl.BLEND)

        for (let i = this.bloomFrameBuffers.length - 2; i >= 0; i--) {
            let baseTex = this.bloomFrameBuffers[i]
            gl.uniform2f(this.factory.bloomBlurProgram.uniforms.texelSize, last.texelSizeX, last.texelSizeY)
            gl.uniform1i(this.factory.bloomBlurProgram.uniforms.uTexture, last.attach(0))
            gl.viewport(0, 0, baseTex.width, baseTex.height)
            this.blit(baseTex.fbo)
            last = baseTex
        }

        gl.disable(gl.BLEND)
        this.factory.bloomFinalProgram.bind(gl)
        gl.uniform2f(this.factory.bloomFinalProgram.uniforms.texelSize, last.texelSizeX, last.texelSizeY)
        gl.uniform1i(this.factory.bloomFinalProgram.uniforms.uTexture, last.attach(0))
        gl.uniform1f(this.factory.bloomFinalProgram.uniforms.intensity, config.BLOOM_INTENSITY)
        gl.viewport(0, 0, destination.width, destination.height)
        this.blit(destination.fbo)
    }

    applySunrays(source, mask, destination) {
        const gl = this.gl
        gl.disable(gl.BLEND)
        this.factory.sunraysMaskProgram.bind(gl)
        gl.uniform1i(this.factory.sunraysMaskProgram.uniforms.uTexture, source.attach(0))
        gl.viewport(0, 0, mask.width, mask.height)
        this.blit(mask.fbo)

        this.factory.sunraysProgram.bind(gl)
        gl.uniform1f(this.factory.sunraysProgram.uniforms.weight, config.SUNRAYS_WEIGHT)
        gl.uniform1i(this.factory.sunraysProgram.uniforms.uTexture, mask.attach(0))
        gl.viewport(0, 0, destination.width, destination.height)
        this.blit(destination.fbo)
    }

    blur(target, temp, iterations) {
        const gl = this.gl
        this.factory.blurProgram.bind(gl)
        for (let i = 0; i < iterations; i++) {
            gl.uniform2f(this.factory.blurProgram.uniforms.texelSize, target.texelSizeX, 0.0)
            gl.uniform1i(this.factory.blurProgram.uniforms.uTexture, target.attach(0))
            this.blit(temp.fbo)

            gl.uniform2f(this.factory.blurProgram.uniforms.texelSize, 0.0, target.texelSizeY)
            gl.uniform1i(this.factory.blurProgram.uniforms.uTexture, temp.attach(0))
            this.blit(target.fbo)
        }
    }

    splatPointer(pointer) {
        let dx = pointer.deltaX * config.SPLAT_FORCE
        let dy = pointer.deltaY * config.SPLAT_FORCE
        this.splat(pointer.texcoordX, pointer.texcoordY, dx, dy, pointer.color)
    }

    multipleSplats(amount) {
        for (let i = 0; i < amount; i++) {
            const color = generateColor()
            color.r *= 10.0
            color.g *= 10.0
            color.b *= 10.0
            const x = Math.random()
            const y = Math.random()
            const dx = 1000 * (Math.random() - 0.5)
            const dy = 1000 * (Math.random() - 0.5)
            this.splat(x, y, dx, dy, color)
        }
    }

    splat(x, y, dx, dy, color) {
        const gl = this.gl
        gl.viewport(0, 0, this.velocity.width, this.velocity.height)
        this.factory.splatProgram.bind(gl)
        gl.uniform1i(this.factory.splatProgram.uniforms.uTarget, this.velocity.read.attach(0))
        gl.uniform1f(this.factory.splatProgram.uniforms.aspectRatio, this.canvas.width / this.canvas.height)
        gl.uniform2f(this.factory.splatProgram.uniforms.point, x, y)
        gl.uniform3f(this.factory.splatProgram.uniforms.color, dx, dy, 0.0)
        gl.uniform1f(this.factory.splatProgram.uniforms.radius, this.correctRadius(config.SPLAT_RADIUS / 100.0))
        this.blit(this.velocity.write.fbo)
        this.velocity.swap()

        gl.viewport(0, 0, this.dye.width, this.dye.height)
        gl.uniform1i(this.factory.splatProgram.uniforms.uTarget, this.dye.read.attach(0))
        gl.uniform3f(this.factory.splatProgram.uniforms.color, color.r, color.g, color.b)
        this.blit(this.dye.write.fbo)
        this.dye.swap()
    }

    correctRadius(radius) {
        const aspectRatio = this.canvas.width / this.canvas.height
        if (aspectRatio > 1) radius *= aspectRatio
        return radius
    }

    captureScreenShot() {
        const gl = this.gl
        let res = getResolution(gl, config.CAPTURE_RESOLUTION)
        let target = createFBO(gl, res.width, res.height, this.ext.formatRGBA.internalFormat, this.ext.formatRGBA.format, this.ext.halfFloatTexType, gl.NEAREST)
        this.render(target)

        let texture = framebufferToTexture(gl, target)
        texture = normalizeTexture(texture, target.width, target.height)

        const dataUri = textureToCanvas(texture, target.width, target.height).toDataURL()
        downloadURI('fluid.png', dataUri)
        window.URL.revokeObjectURL(dataUri)
    }
}
