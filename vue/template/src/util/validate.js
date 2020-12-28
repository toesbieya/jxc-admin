export function isString(str) {
    return typeof str === 'string' || str instanceof String
}

export function isExternal(path) {
    return /^(https?:|mailto:|tel:)/.test(path)
}
