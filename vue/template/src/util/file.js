import request from "@/api/request"
import {getToken} from "@/api/file"
import {file as fileConfig} from '@/config'
import {isEmpty, timeFormat} from "@/util"
import {isTxt} from "@/util/validate"

const defaultOptions = {
    headers: {"Content-Type": "multipart/form-data"},
    generateKey(filename) {
        return timeFormat('yyyy/MM/dd/') + Date.now() + '/' + filename
    },
    onUploadProgress(e) {

    }
}

//文件预览
export function preview(url) {
    if (isTxt(url)) return window.open(url)

    const connectChar = url.includes('?') ? '&' : '?'
    url = url + connectChar + 'fullfilename=' + url.replace(fileConfig.storePrefix, '')
    const anchor = document.createElement('a')
    anchor.style.opacity = '0'
    anchor.href = `${fileConfig.previewPrefix}/onlinePreview?url=${encodeURIComponent(url)}`
    anchor.target = '_blank'
    document.body.appendChild(anchor)
    anchor.click()
    document.body.removeChild(anchor)
}

//下载文件
export function download(url, name) {
    const href = typeof url === 'object' ? window.URL.createObjectURL(url) : url
    const anchor = document.createElement('a')

    anchor.style.opacity = '0'
    anchor.href = href
    anchor.download = name
    document.body.appendChild(anchor)
    anchor.click()
    document.body.removeChild(anchor)

    if (typeof url === 'object') window.URL.revokeObjectURL(url)
}

//七牛云直传
export function upload(blob, filename, options = {}) {
    return getToken
        .request()
        .then(token => {
            if (!options.generateKey) {
                options.generateKey = defaultOptions.generateKey
            }
            const param = new FormData()
            param.append('token', token)
            param.append('key', options.generateKey(filename))
            param.append('file', blob, filename)
            return request.post(fileConfig.uploadUrl, param, {...defaultOptions, ...options})
        })
}

//自动补全附件链接前缀
export function autoCompleteUrl(url) {
    if (isEmpty(url)) return ''
    if (url.startsWith('http')) return url
    return fileConfig.storePrefix + url
}
