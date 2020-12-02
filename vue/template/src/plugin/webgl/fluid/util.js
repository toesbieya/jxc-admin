function getWebGLContext(canvas) {
    const params = {alpha: true, depth: false, stencil: false, antialias: false, preserveDrawingBuffer: false}

    let gl = canvas.getContext('webgl2', params)
    const isWebGL2 = !!gl
    if (!isWebGL2) gl = canvas.getContext('webgl', params) || canvas.getContext('experimental-webgl', params)

    let halfFloat
    let supportLinearFiltering
    if (isWebGL2) {
        gl.getExtension('EXT_color_buffer_float')
        supportLinearFiltering = gl.getExtension('OES_texture_float_linear')
    }
    else {
        halfFloat = gl.getExtension('OES_texture_half_float')
        supportLinearFiltering = gl.getExtension('OES_texture_half_float_linear')
    }

    gl.clearColor(0.0, 0.0, 0.0, 1.0)

    const halfFloatTexType = isWebGL2 ? gl.HALF_FLOAT : halfFloat.HALF_FLOAT_OES
    let formatRGBA
    let formatRG
    let formatR

    if (isWebGL2) {
        formatRGBA = getSupportedFormat(gl, gl.RGBA16F, gl.RGBA, halfFloatTexType)
        formatRG = getSupportedFormat(gl, gl.RG16F, gl.RG, halfFloatTexType)
        formatR = getSupportedFormat(gl, gl.R16F, gl.RED, halfFloatTexType)
    }
    else {
        formatRGBA = getSupportedFormat(gl, gl.RGBA, gl.RGBA, halfFloatTexType)
        formatRG = getSupportedFormat(gl, gl.RGBA, gl.RGBA, halfFloatTexType)
        formatR = getSupportedFormat(gl, gl.RGBA, gl.RGBA, halfFloatTexType)
    }

    return {
        gl,
        ext: {
            formatRGBA,
            formatRG,
            formatR,
            halfFloatTexType,
            supportLinearFiltering
        }
    }
}

function getSupportedFormat(gl, internalFormat, format, type) {
    if (!supportRenderTextureFormat(gl, internalFormat, format, type)) {
        switch (internalFormat) {
            case gl.R16F:
                return getSupportedFormat(gl, gl.RG16F, gl.RG, type)
            case gl.RG16F:
                return getSupportedFormat(gl, gl.RGBA16F, gl.RGBA, type)
            default:
                return null
        }
    }

    return {internalFormat, format}
}

function supportRenderTextureFormat(gl, internalFormat, format, type) {
    let texture = gl.createTexture()
    gl.bindTexture(gl.TEXTURE_2D, texture)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE)
    gl.texImage2D(gl.TEXTURE_2D, 0, internalFormat, 4, 4, 0, format, type, null)

    let fbo = gl.createFramebuffer()
    gl.bindFramebuffer(gl.FRAMEBUFFER, fbo)
    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, texture, 0)

    const status = gl.checkFramebufferStatus(gl.FRAMEBUFFER)
    return status === gl.FRAMEBUFFER_COMPLETE
}

function isMobile() {
    return /Mobi|Android/i.test(navigator.userAgent)
}

function addKeywords(source, keywords) {
    if (keywords == null) return source
    let keywordsString = ''
    keywords.forEach(keyword => {
        keywordsString += '#define ' + keyword + '\n'
    })
    return keywordsString + source
}

function compileShader(gl, type, source, keywords) {
    source = addKeywords(source, keywords)

    const shader = gl.createShader(type)
    gl.shaderSource(shader, source)
    gl.compileShader(shader)

    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) throw gl.getShaderInfoLog(shader)

    return shader
}

function createProgram(gl, vertexShader, fragmentShader) {
    let program = gl.createProgram()
    gl.attachShader(program, vertexShader)
    gl.attachShader(program, fragmentShader)
    gl.linkProgram(program)

    if (!gl.getProgramParameter(program, gl.LINK_STATUS)) throw gl.getProgramInfoLog(program)

    return program
}

function getUniforms(gl, program) {
    let uniforms = []
    let uniformCount = gl.getProgramParameter(program, gl.ACTIVE_UNIFORMS)
    for (let i = 0; i < uniformCount; i++) {
        let uniformName = gl.getActiveUniform(program, i).name
        uniforms[uniformName] = gl.getUniformLocation(program, uniformName)
    }
    return uniforms
}

function getResolution(gl, resolution) {
    let aspectRatio = gl.drawingBufferWidth / gl.drawingBufferHeight
    if (aspectRatio < 1) aspectRatio = 1.0 / aspectRatio

    let min = Math.round(resolution)
    let max = Math.round(resolution * aspectRatio)

    if (gl.drawingBufferWidth > gl.drawingBufferHeight) return {width: max, height: min}
    else return {width: min, height: max}
}

function clamp01(input) {
    return Math.min(Math.max(input, 0), 1)
}

function normalizeTexture(texture, width, height) {
    let result = new Uint8Array(texture.length)
    let id = 0
    for (let i = height - 1; i >= 0; i--) {
        for (let j = 0; j < width; j++) {
            let nid = i * width * 4 + j * 4
            result[nid] = clamp01(texture[id]) * 255
            result[nid + 1] = clamp01(texture[id + 1]) * 255
            result[nid + 2] = clamp01(texture[id + 2]) * 255
            result[nid + 3] = clamp01(texture[id + 3]) * 255
            id += 4
        }
    }
    return result
}

function framebufferToTexture(gl, target) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, target.fbo)
    let length = target.width * target.height * 4
    let texture = new Float32Array(length)
    gl.readPixels(0, 0, target.width, target.height, gl.RGBA, gl.FLOAT, texture)
    return texture
}

function textureToCanvas(texture, width, height) {
    let captureCanvas = document.createElement('canvas')
    let ctx = captureCanvas.getContext('2d')
    captureCanvas.width = width
    captureCanvas.height = height

    let imageData = ctx.createImageData(width, height)
    imageData.data.set(texture)
    ctx.putImageData(imageData, 0, 0)

    return captureCanvas
}

function downloadURI(filename, uri) {
    let link = document.createElement('a')
    link.download = filename
    link.href = uri
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
}

function createFBO(gl, w, h, internalFormat, format, type, param) {
    gl.activeTexture(gl.TEXTURE0)
    let texture = gl.createTexture()
    gl.bindTexture(gl.TEXTURE_2D, texture)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, param)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, param)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE)
    gl.texImage2D(gl.TEXTURE_2D, 0, internalFormat, w, h, 0, format, type, null)

    let fbo = gl.createFramebuffer()
    gl.bindFramebuffer(gl.FRAMEBUFFER, fbo)
    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, texture, 0)
    gl.viewport(0, 0, w, h)
    gl.clear(gl.COLOR_BUFFER_BIT)

    let texelSizeX = 1.0 / w
    let texelSizeY = 1.0 / h

    return {
        texture, fbo, width: w, height: h, texelSizeX, texelSizeY, attach(id) {
            gl.activeTexture(gl.TEXTURE0 + id)
            gl.bindTexture(gl.TEXTURE_2D, texture)
            return id
        }
    }
}

function createDoubleFBO(gl, w, h, internalFormat, format, type, param) {
    let fbo1 = createFBO(gl, w, h, internalFormat, format, type, param)
    let fbo2 = createFBO(gl, w, h, internalFormat, format, type, param)

    return {
        width: w,
        height: h,
        texelSizeX: fbo1.texelSizeX,
        texelSizeY: fbo1.texelSizeY,
        get read() {
            return fbo1
        },
        set read(value) {
            fbo1 = value
        },
        get write() {
            return fbo2
        },
        set write(value) {
            fbo2 = value
        },
        swap() {
            let temp = fbo1
            fbo1 = fbo2
            fbo2 = temp
        }
    }
}

function HSVtoRGB(h, s, v) {
    let r, g, b, i, f, p, q, t
    i = Math.floor(h * 6)
    f = h * 6 - i
    p = v * (1 - s)
    q = v * (1 - f * s)
    t = v * (1 - (1 - f) * s)

    switch (i % 6) {
        case 0:
            r = v
            g = t
            b = p
            break
        case 1:
            r = q
            g = v
            b = p
            break
        case 2:
            r = p
            g = v
            b = t
            break
        case 3:
            r = p
            g = q
            b = v
            break
        case 4:
            r = t
            g = p
            b = v
            break
        case 5:
            r = v
            g = p
            b = q
            break
    }

    return {r, g, b}
}

function generateColor() {
    let c = HSVtoRGB(Math.random(), 1.0, 1.0)
    c.r *= 0.15
    c.g *= 0.15
    c.b *= 0.15
    return c
}

function scaleByPixelRatio(input) {
    let pixelRatio = window.devicePixelRatio || 1
    return Math.floor(input * pixelRatio)
}

function wrap(value, min, max) {
    let range = max - min
    if (range === 0) return min
    return ((value - min) % range) + min
}

function normalizeColor(input) {
    return {
        r: input.r / 255,
        g: input.g / 255,
        b: input.b / 255
    }
}

function correctDeltaX(canvas, delta) {
    let aspectRatio = canvas.width / canvas.height
    if (aspectRatio < 1) delta *= aspectRatio
    return delta
}

function correctDeltaY(canvas, delta) {
    let aspectRatio = canvas.width / canvas.height
    if (aspectRatio > 1) delta /= aspectRatio
    return delta
}

function updatePointerDownData(canvas, pointer, id, posX, posY) {
    pointer.id = id
    pointer.down = true
    pointer.moved = false
    pointer.texcoordX = posX / canvas.width
    pointer.texcoordY = 1.0 - posY / canvas.height
    pointer.prevTexcoordX = pointer.texcoordX
    pointer.prevTexcoordY = pointer.texcoordY
    pointer.deltaX = 0
    pointer.deltaY = 0
    pointer.color = generateColor()
}

function updatePointerMoveData(canvas, pointer, posX, posY) {
    pointer.moved = pointer.down
    pointer.prevTexcoordX = pointer.texcoordX
    pointer.prevTexcoordY = pointer.texcoordY
    pointer.texcoordX = posX / canvas.width
    pointer.texcoordY = 1.0 - posY / canvas.height
    pointer.deltaX = correctDeltaX(canvas, pointer.texcoordX - pointer.prevTexcoordX)
    pointer.deltaY = correctDeltaY(canvas, pointer.texcoordY - pointer.prevTexcoordY)
}

function updatePointerUpData(pointer) {
    pointer.down = false
}

function resizeCanvas(canvas) {
    let width = scaleByPixelRatio(canvas.clientWidth)
    let height = scaleByPixelRatio(canvas.clientHeight)
    if (canvas.width !== width || canvas.height !== height) {
        canvas.width = width
        canvas.height = height
        return true
    }
    return false
}

export {
    getWebGLContext,
    isMobile,
    compileShader,
    createProgram,
    getUniforms,
    getResolution,
    normalizeTexture,
    framebufferToTexture,
    textureToCanvas,
    downloadURI,
    createFBO,
    createDoubleFBO,
    generateColor,
    scaleByPixelRatio,
    wrap,
    normalizeColor,
    updatePointerDownData,
    updatePointerMoveData,
    updatePointerUpData,
    resizeCanvas,
}
