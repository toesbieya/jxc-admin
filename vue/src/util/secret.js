import pako from 'pako'
import md5 from 'js-md5'

export {md5}

/**
 * 解压字符串
 *
 * @param b64Data {string} base64编码的字符串
 * @return {string}
 */
export function unzip(b64Data) {
    let strData = window.atob(b64Data)
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

/**
 * 压缩字符串
 *
 * @param str {string}
 * @return {string}
 */
export function zip(str) {
    const binaryString = pako.gzip(encodeURIComponent(str), {to: 'string'})
    return window.btoa(binaryString)
}
