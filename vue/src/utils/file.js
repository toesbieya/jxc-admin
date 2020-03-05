import request from '@/config/request'
import {attachmentUploadUrl, attachmentPrefix} from '@/config'
import {isEmpty, timeFormat} from "@/utils"

const baseUrl = '/file'

const formConfig = {
    headers: {"Content-Type": "multipart/form-data"}
}

//获取七牛云直传需要的token
export function getToken() {
    return request.get(baseUrl + '/getToken').then(({data}) => data.data)
}

//下载文件
export function download(url, name) {
    let href = typeof url === 'object' ? window.URL.createObjectURL(url) : url
    let anchor = document.createElement('a')
    anchor.style.visibility = 'hidden'
    anchor.href = href
    anchor.download = name
    document.body.appendChild(anchor)
    anchor.click()
    document.body.removeChild(anchor)
    if (typeof url === 'object') window.URL.revokeObjectURL(url)
}

export function deleteUpload(url) {
    return request.get(baseUrl + '/delete?url=' + encodeURIComponent(url))
}

//七牛云直传
export function upload(blob, filename = '快照.png') {
    return getToken()
        .then(token => {
            let now = new Date()
            let param = new FormData()
            param.append('token', token)
            param.append('key', timeFormat('yyyy/MM/dd/') + now.getTime() + '/' + filename)
            param.append('file', blob, filename)
            return request.post(attachmentUploadUrl, param, {...formConfig, baseUrl: ''})
        })
        .then(({data}) => data.key)
}

//自动补全附件链接前缀
export function autoCompleteUrl(url) {
    if (isEmpty(url)) return ''
    if (['http', 'https'].some(i => url.startsWith(i))) return url
    return attachmentPrefix + url
}
