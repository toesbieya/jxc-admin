import {createProgram, getUniforms} from "./util"

class Program {
    constructor(gl, vertexShader, fragmentShader) {
        this.uniforms = {}
        this.program = createProgram(gl, vertexShader, fragmentShader)
        this.uniforms = getUniforms(gl, this.program)
    }

    bind(gl) {
        gl.useProgram(this.program)
    }
}

export default Program
