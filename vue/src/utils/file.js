import request from '@/config/request'
import {attachmentUploadUrl, attachmentPrefix, filePreviewPrefix} from '@/config'
import {isEmpty, timeFormat} from "@/utils"

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
    const connectChar = url.includes('?') ? '&' : '?'
    url = url + connectChar + 'fullfilename=' + url.replace(attachmentPrefix, '')
    const anchor = document.createElement('a')
    anchor.style.visibility = 'hidden'
    anchor.href = `${filePreviewPrefix}/onlinePreview?url=${encodeURIComponent(url)}`
    anchor.target = '_blank'
    document.body.appendChild(anchor)
    anchor.click()
    document.body.removeChild(anchor)
}

//获取七牛云直传需要的token
export function getToken() {
    return request.get('/file/getToken').then(({data}) => data.data)
}

//下载文件
export function download(url, name) {
    const href = typeof url === 'object' ? window.URL.createObjectURL(url) : url
    const anchor = document.createElement('a')

    anchor.style.visibility = 'hidden'
    anchor.href = href
    anchor.download = name
    document.body.appendChild(anchor)
    anchor.click()
    document.body.removeChild(anchor)

    if (typeof url === 'object') window.URL.revokeObjectURL(url)
}

//删除已上传的文件
export function deleteUpload(url) {
    return request.get('/file/delete?url=' + encodeURIComponent(url))
}

//七牛云直传
export function upload(blob, filename, options = {}) {
    return getToken()
        .then(token => {
            if (!options.generateKey) options.generateKey = defaultOptions.generateKey

            const param = new FormData()
            param.append('token', token)
            param.append('key', options.generateKey(filename))
            param.append('file', blob, filename)
            return request.post(attachmentUploadUrl, param, {...defaultOptions, ...options})
        })
        .then(({data}) => data)
}

//自动补全附件链接前缀
export function autoCompleteUrl(url) {
    if (isEmpty(url)) return ''
    if (['http', 'https'].some(i => url.startsWith(i))) return url
    return attachmentPrefix + url
}
