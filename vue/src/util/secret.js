import pako from 'pako'

export function unzip(b64Data) {
    let strData = atob(b64Data)
    // Convert binary string to character-number array
    let charData = strData.split('').map(x => x.charCodeAt(0))
    // Turn number array into byte-array
    let binData = new Uint8Array(charData)
    // unzip
    let data = pako.inflate(binData)
    // Convert gunzipped byteArray back to ascii string:
    strData = String.fromCharCode.apply(null, new Uint16Array(data))
    return decodeURIComponent(strData)
}

export function zip(str) {
    let binaryString = pako.gzip(encodeURIComponent(str), {to: 'string'})
    return btoa(binaryString)
}
