export function isString(str) {
    return typeof str === 'string' || str instanceof String
}

export function isInteger(v) {
    const t = parseFloat(v)
    return t.toString() !== 'NaN' && t < 2147483647 && t > -2147483648
}

export function isExternal(path) {
    return /^(https?:|mailto:|tel:)/.test(path)
}

export function isEmail(email) {
    const reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    return reg.test(email)
}

export function isImage(suffix) {
    const reg = /\.(png|jpg|gif|jpeg|webp|bmp)$/
    return reg.test(suffix.toLowerCase())
}

export function isDoc(suffix) {
    return /\.(doc|docx)$/.test(suffix.toLowerCase())
}

export function isPdf(suffix) {
    return /\.pdf$/.test(suffix.toLowerCase())
}

export function isPpt(suffix) {
    return /\.(ppt|pptx)$/.test(suffix.toLowerCase())
}

export function isRar(suffix) {
    return /\.rar$/.test(suffix.toLowerCase())
}

export function isXls(suffix) {
    return /\.(xls|xlsx)$/.test(suffix.toLowerCase())
}

export function isTxt(suffix) {
    return /\.txt$/.test(suffix.toLowerCase())
}

export function isZip(suffix) {
    return /\.zip$/.test(suffix.toLowerCase())
}
