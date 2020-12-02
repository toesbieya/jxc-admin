export function base64ToUint8(base64) {
    let bstr = atob(base64.split(',')[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n)
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
    }
    return u8arr
}

export function uint8Array2base64(o) {
    let g = ""
    let j = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    let r = new Uint8Array(o)
    let p = r.byteLength
    let f = p % 3
    let q = p - f
    let n, l, k, h
    let m
    for (let e = 0; e < q; e = e + 3) {
        m = (r[e] << 16) | (r[e + 1] << 8) | r[e + 2]
        n = (m & 16515072) >> 18
        l = (m & 258048) >> 12
        k = (m & 4032) >> 6
        h = m & 63
        g += j[n] + j[l] + j[k] + j[h]
    }
    if (f === 1) {
        m = r[q]
        n = (m & 252) >> 2
        l = (m & 3) << 4
        g += j[n] + j[l] + "=="
    }
    else if (f === 2) {
        m = (r[q] << 8) | r[q + 1]
        n = (m & 16128) >> 8
        l = (m & 1008) >> 4
        k = (m & 15) << 2
        g += j[n] + j[l] + j[k] + "="
    }
    return "data:image/jpeg;base64," + g
}
