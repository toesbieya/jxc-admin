<template>
    <div :class="{'upload-file': true, 'is-disabled': disabled}">
        <div
            v-for="file in fileList"
            :key="file.id"
            :class="`upload-file-item is-${file.status}`"
            :title="file.name"
        >
            <img :src="file.thumbnailUrl">

            <div v-if="file.status === 'uploading'" class="upload-file-item-loading">
                <i class="el-icon-loading"/>
                <p>上传中</p>
            </div>

            <template v-else>
                <div class="upload-file-item-action">
                    <template v-if="file.status === 'success'">
                        <i class="el-icon-zoom-in" title="预览" @click="handlePreview(file)"/>
                        <i class="el-icon-download" title="下载" @click="handleDownload(file)"/>
                    </template>
                    <i v-if="!disabled" class="el-icon-delete" title="删除" @click="handleRemove(file)"/>
                </div>

                <label :class="`upload-file-item-status-label`">
                    <i :class="`el-icon-${file.status==='success'?'check':'close'}`"/>
                </label>
            </template>
        </div>

        <div v-if="!hideTrigger" class="upload-file-trigger" @click="handleTriggerClick">
            <i class="el-icon-plus"/>
            <input ref="input" type="file" :multiple="multiple" :accept="accept" @change="handleInputChange">
        </div>
    </div>
</template>

<script>
import ajax from 'element-ui/packages/upload/src/ajax'

export default {
    name: "UploadCardList",

    props: {
        //上传的地址
        action: String,
        //设置上传的请求头部，为函数时入参为当前上传的文件file
        headers: [Object, Function],
        //上传时附带的额外参数，为函数时入参为当前上传的文件file
        data: [Object, Function],
        //上传时是否携带cookie
        withCredentials: Boolean,
        //上传的文件字段名
        name: {type: String, default: 'file'},
        //发送文件上传请求的实现
        httpRequest: {type: Function, default: ajax},

        //是否支持多选文件
        multiple: Boolean,
        //接受上传的文件类型，即input的accept属性
        accept: String,
        //已上传的文件列表
        uploadedFileList: {type: Array, default: () => []},
        //是否禁用
        disabled: Boolean,
        //是否在禁用时隐藏上传按钮
        hideTriggerOnDisabled: {type: Boolean, default: true},
        //最大允许上传个数，<1时不作限制
        limit: {type: Number, default: 0},
        //是否在已上传个数等于最大上传个数时隐藏上传按钮
        hideTriggerOnExceed: {type: Boolean, default: true},
        //是否在文件上传失败时立即删除
        autoDelete: {type: Boolean, default: true},
        //最大可上传的文件大小，小于1时不作限制，Number型单位为B，String型后缀支持KB、MB
        maxSize: {type: [Number, String], default: 0},

        //上传文件之前的钩子，参数为上传的文件，若返回 false 或者返回 Promise 且被 reject，则停止上传
        beforeUpload: Function,
        //删除文件之前的钩子，参数为上传的文件、文件列表，若返回 false 或者返回 Promise 且被 reject，则停止删除
        beforeRemove: Function
    },

    data() {
        //用于确定上传文件的唯一id
        this.tempId = 1
        //上传请求哈希表：<file.id, XHR>
        this.requestMap = {}

        return {
            //文件列表，包含已上传、未上传、正在上传、上传失败的文件
            fileList: []
        }
    },

    computed: {
        needLimit() {
            return this.limit >= 1
        },

        hideTrigger() {
            return this.disabled && this.hideTriggerOnDisabled
                || this.needLimit && this.fileList.length >= this.limit && this.hideTriggerOnExceed
        }
    },

    watch: {
        uploadedFileList: {
            immediate: true,
            handler(arr) {
                //取消正在上传的文件
                Object.entries(this.requestMap).forEach(([id, xhr]) => {
                    if (arr.every(file => file.id !== id)) {
                        //如果是自定义的上传请求，可能没有实现abort
                        xhr.abort && xhr.abort()
                        delete this.requestMap[id]
                    }
                })

                this.fileList = arr.map(item => ({
                    ...item,
                    id: item.id || this.tempId++,
                    status: item.status || 'success',
                    thumbnailUrl: item.thumbnailUrl || item.url,
                    downloadUrl: item.thumbnailUrl || item.url
                }))
            }
        }
    },

    methods: {
        parseMaxSize(maxSize) {
            if (typeof maxSize === 'number') {
                return maxSize
            }

            const map = {KB: 1024, MB: 1024 * 1024},
                upper = maxSize.toUpperCase(),
                num = upper.replace(/[^0-9]/ig, ""),
                unit = upper.replace(num, "")

            return parseFloat(num) * (map[unit] || 1)
        },

        //点击上传按钮
        handleTriggerClick() {
            if (this.disabled) return

            this.$refs.input.value = null
            this.$refs.input.click()
        },
        //隐藏的input的值改变
        handleInputChange(e) {
            const files = e.target.files

            if (!files || files.length === 0) return

            if (this.needLimit && this.fileList.length + files.length > this.limit) {
                return this.$emit('exceed')
            }

            Array.from(files).forEach(this.upload)
        },
        //点击预览按钮
        handlePreview(file) {
            this.$emit('preview', file)
        },
        //点击下载按钮
        handleDownload(file) {
            this.$emit('download', file)
        },
        //点击删除按钮
        handleRemove(file) {
            this.remove(file)
        },

        //上传单个文件
        upload(rawFile) {
            //判断文件大小是否超出限制
            const maxSize = this.parseMaxSize(this.maxSize)
            if (maxSize >= 1 && rawFile.size > maxSize) {
                return this.$emit('over-max-size', rawFile)
            }

            rawFile.id = this.tempId++

            const file = {
                id: rawFile.id,
                name: rawFile.name,
                thumbnailUrl: rawFile.type.startsWith('image') && window.URL.createObjectURL(rawFile),
                status: 'uploading',
                percentage: 0,
                raw: rawFile
            }

            this.fileList.push(file)

            if (!this.beforeUpload) {
                return this.sendRequest(file)
            }

            const result = this.beforeUpload(file)

            if (!result) {
                return this.removeFile(file)
            }

            result.then
                ? result.then(() => this.sendRequest(file), () => this.removeFile(file))
                : this.sendRequest(file)
        },
        sendRequest(file) {
            const {id, raw} = file
            const headers = typeof this.headers === 'function' ? this.headers(file) : this.headers
            const data = typeof this.data === 'function' ? this.data(file) : this.data
            const options = {
                headers,
                data,
                withCredentials: this.withCredentials,
                file: raw,
                filename: this.name,
                action: this.action,
                onProgress: e => {
                    this.onProgress(e, file)
                },
                onSuccess: res => {
                    this.onSuccess(res, file)
                    delete this.requestMap[id]
                },
                onError: err => {
                    this.onError(err, file)
                    delete this.requestMap[id]
                }
            }
            const req = this.httpRequest(options)
            this.requestMap[id] = req
            req && req.then && req.then(options.onSuccess, options.onError)
        },
        abortRequest(file) {
            const map = this.requestMap

            if (file) {
                const req = map[file.id]
                req && req.abort && req.abort()
                delete map[file.id]
                return
            }

            Object.entries(map).forEach(([id, req]) => {
                req && req.abort && req.abort()
                delete map[id]
            })
        },
        onProgress(e, file) {
            file.status = 'uploading'
            file.percentage = e.percent || 0
        },
        onSuccess(res, file) {
            file.status = 'success'
            file.response = res
            this.$emit('success', res, file)
        },
        onError(err, file) {
            file.status = 'error'
            if (!file.response) file.response = {}
            file.response.err = err
            this.autoDelete && this.removeFile(file)
            this.$emit('error', err, file)
        },

        //删除文件
        remove(file) {
            if (!this.beforeRemove) {
                return this.removeFile(file)
            }

            const result = this.beforeRemove(file)

            if (!result) return

            result.then
                ? result.then(() => this.removeFile(file), () => ({}))
                : this.removeFile(file)
        },
        removeFile(file) {
            this.revokeBlob(file)
            this.fileList.splice(this.fileList.indexOf(file), 1)
            this.$emit('remove', file)
        },
        revokeBlob({thumbnailUrl, downloadUrl}) {
            if (thumbnailUrl && thumbnailUrl.startsWith('blob:')) {
                window.URL.revokeObjectURL(thumbnailUrl)
            }
            if (downloadUrl && downloadUrl.startsWith('blob:')) {
                window.URL.revokeObjectURL(downloadUrl)
            }
        }
    },

    beforeDestroy() {
        //取消正在上传的文件
        this.abortRequest()

        //释放上传文件的本地缓存
        this.fileList.forEach(this.revokeBlob)
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
