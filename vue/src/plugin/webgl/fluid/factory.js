import Material from './Material'
import Program from './Program'
import {
    advectionShader,
    baseVertexShader,
    bloomBlurShader,
    bloomFinalShader,
    bloomPrefilterShader,
    blurShader,
    blurVertexShader,
    checkerboardShader,
    clearShader,
    colorShader,
    copyShader,
    curlShader,
    displayShaderSource,
    divergenceShader,
    gradientSubtractShader,
    pressureShader,
    splatShader,
    sunraysMaskShader,
    sunraysShader,
    vorticityShader
} from './shader'

export default function (gl, ext) {
    const blurProgram = new Program(gl, blurVertexShader(gl), blurShader(gl))
    const copyProgram = new Program(gl, baseVertexShader(gl), copyShader(gl))
    const clearProgram = new Program(gl, baseVertexShader(gl), clearShader(gl))
    const colorProgram = new Program(gl, baseVertexShader(gl), colorShader(gl))
    const checkerboardProgram = new Program(gl, baseVertexShader(gl), checkerboardShader(gl))
    const bloomPrefilterProgram = new Program(gl, baseVertexShader(gl), bloomPrefilterShader(gl))
    const bloomBlurProgram = new Program(gl, baseVertexShader(gl), bloomBlurShader(gl))
    const bloomFinalProgram = new Program(gl, baseVertexShader(gl), bloomFinalShader(gl))
    const sunraysMaskProgram = new Program(gl, baseVertexShader(gl), sunraysMaskShader(gl))
    const sunraysProgram = new Program(gl, baseVertexShader(gl), sunraysShader(gl))
    const splatProgram = new Program(gl, baseVertexShader(gl), splatShader(gl))
    const advectionProgram = new Program(gl, baseVertexShader(gl), advectionShader(gl, ext))
    const divergenceProgram = new Program(gl, baseVertexShader(gl), divergenceShader(gl))
    const curlProgram = new Program(gl, baseVertexShader(gl), curlShader(gl))
    const vorticityProgram = new Program(gl, baseVertexShader(gl), vorticityShader(gl))
    const pressureProgram = new Program(gl, baseVertexShader(gl), pressureShader(gl))
    const gradienSubtractProgram = new Program(gl, baseVertexShader(gl), gradientSubtractShader(gl))
    const displayMaterial = new Material(baseVertexShader(gl), displayShaderSource(gl))
    return {
        blurProgram, copyProgram, clearProgram, colorProgram, checkerboardProgram, bloomPrefilterProgram,
        bloomBlurProgram, bloomFinalProgram, sunraysMaskProgram, sunraysProgram, splatProgram, advectionProgram,
        divergenceProgram, curlProgram, vorticityProgram, pressureProgram, gradienSubtractProgram, displayMaterial
    }
}
