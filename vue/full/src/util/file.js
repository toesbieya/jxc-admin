import {saveAs} from 'file-saver'
import {encode} from 'js-base64'
import request from "@/api/request"
import {getToken} from "@/api/file"
import {file as fileConfig} from '@/config'
import store from '@/store'
import {isEmpty, timeFormat} from "@/util"
import {isExternal, isTxt} from "@/util/validate"

const defaultOptions = {
    headers: {"Content-Type": "multipart/form-data"},
    generateKey(filename) {
        return `${timeFormat('yyyy/MM/dd')}/user${store.state.user.id}/${Date.now()}/${filename}`
    },
    onUploadProgress(e) {

    }
}

//文件预览
export function preview(url) {
    if (isTxt(url)) return window.open(url)

    url = encodeURIComponent(encode(url))

    const anchor = document.createElement('a')
    anchor.style.display = 'none'
    anchor.href = `${fileConfig.previewPrefix}/onlinePreview?url=${url}`
    anchor.target = '_blank'
    document.body.appendChild(anchor)
    anchor.click()
    document.body.removeChild(anchor)
}

//下载文件
export function download(url, name) {
    saveAs(url, name)
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
    if (isExternal(url)) return url
    return fileConfig.storePrefix + url
}
