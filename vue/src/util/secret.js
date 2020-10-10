import pako from 'pako'
import md5 from 'js-md5'

export {md5}

export function unzip(b64Data) {
    let strData = atob(b64Data)
    // Convert binary string to character-number array
    const charData = strData.split('').map(x => x.charCodeAt(0))
    // Turn number array into byte-array
    const binData = new Uint8Array(charData)
    // unzip
    const data = pako.inflate(binData)
    // Convert gunzipped byteArray back to ascii string:
    strData = String.fromCharCode.apply(null, new Uint16Array(data))
    return decodeURIComponent(strData)
}

export function zip(str) {
    const binaryString = pako.gzip(encodeURIComponent(str), {to: 'string'})
    return btoa(binaryString)
}
