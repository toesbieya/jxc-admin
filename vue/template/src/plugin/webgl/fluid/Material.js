import {compileShader, createProgram, getUniforms} from "./util"

class Material {
    constructor(vertexShader, fragmentShaderSource) {
        this.vertexShader = vertexShader
        this.fragmentShaderSource = fragmentShaderSource
        this.programs = []
        this.activeProgram = null
        this.uniforms = []
    }

    setKeywords(gl, keywords) {
        let hash = 0
        for (let i = 0; i < keywords.length; i++) hash += hashCode(keywords[i])

        let program = this.programs[hash]
        if (program == null) {
            let fragmentShader = compileShader(gl, gl.FRAGMENT_SHADER, this.fragmentShaderSource, keywords)
            program = createProgram(gl, this.vertexShader, fragmentShader)
            this.programs[hash] = program
        }

        if (program === this.activeProgram) return

        this.uniforms = getUniforms(gl, program)
        this.activeProgram = program
    }

    bind(gl) {
        gl.useProgram(this.activeProgram)
    }
}

function hashCode(s) {
    if (s.length === 0) return 0
    let hash = 0
    for (let i = 0; i < s.length; i++) {
        hash = (hash << 5) - hash + s.charCodeAt(i)
        hash |= 0 // Convert to 32bit integer
    }
    return hash
}

export default Material
