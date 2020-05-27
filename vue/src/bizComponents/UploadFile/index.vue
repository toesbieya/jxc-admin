<template>
    <el-upload
            ref="upload"
            :before-upload="beforeUpload"
            :class="{disabled:hideUploader}"
            :file-list="data"
            :http-request="httpRequest"
            :limit="limit"
            :multiple="multiple"
            :on-error="error"
            :on-exceed="exceed"
            :on-success="success"
            action=""
            list-type="picture-card"
    >
        <i class="el-icon-plus" slot="default"/>
        <template v-slot:file="{file}">
            <el-tooltip :content="file.name" effect="dark" placement="bottom">
                    <span class="el-upload-list__item-actions">
                        <span v-if="showPreview(file)" class="el-upload-list__item-preview" @click="preview(file)">
                            <i class="el-icon-zoom-in"/>
                        </span>
                        <span v-if="showDownload(file)" class="el-upload-list__item-delete" @click="download(file)">
                            <i class="el-icon-download"/>
                        </span>
                        <span v-if="!disabled" class="el-upload-list__item-delete" @click="remove(file)">
                            <i class="el-icon-delete"/>
                        </span>
                    </span>
            </el-tooltip>
            <img :src="file.url" class="el-upload-list__item-thumbnail">
            <label class="el-upload-list__item-status-label">
                <i class="el-icon-upload-success el-icon-check"/>
            </label>
            <div v-if="file.status==='uploading'" class="progress-mask">
                <el-progress :percentage="file.percentage" type="circle"/>
            </div>
        </template>
    </el-upload>
</template>
<script>
    /*
    * 直传七牛云
    * 传入fileList自动拼接七牛云外链前缀
    * success、remove事件中的file.url均不带七牛云外链前缀
    * */
    import axios from 'axios'
    import {attachmentPrefix} from '@/config'
    import {elError} from "@/utils/message"
    import {isEmpty} from '@/utils'
    import {isImage} from "@/utils/validate"
    import {deleteUpload, download, upload, autoCompleteUrl} from "@/utils/file"
    import {numberFormatter} from "@/filter"

    export default {
        name: 'UploadFile',
        props: {
            disabled: {type: Boolean, default: false},
            multiple: {type: Boolean, default: true},
            fileList: {type: Array, default: () => []},
            limit: {type: Number, default: 10},
            maxSize: {type: Number | String, default: '10MB'}
        },
        data() {
            return {
                data: this.fileList,
                index: 0,
                show: false
            }
        },
        computed: {
            count() {
                return this.data.length
            },
            hideUploader() {
                return this.disabled || this.count >= this.limit
            },
            previewUrlList() {
                if (!this.data) return []
                return this.data.map(i => i.url)
            }
        },
        watch: {
            fileList: {
                immediate: true,
                handler(fileList) {
                    if (isEmpty(fileList)) {
                        this.data = []
                        return
                    }
                    this.data = [...this.fileList.map(file => ({...file, url: autoCompleteUrl(file.url)}))]

                    //更新前保留预览图片的index
                    if (this.data[this.index]) {
                        this.index = this.data.findIndex(i => i === this.data[this.index])
                    }
                }
            }
        },
        methods: {
            showPreview(file) {
                return file.name && isImage(file.name)
            },
            showDownload(file) {
                return file.name && !isImage(file.name)
            },
            parseMaxSize(maxSize) {
                if (typeof maxSize === 'number') {
                    return maxSize
                }
                const map = {KB: 1024, MB: 1024 * 1024}
                let upper = maxSize.toUpperCase()
                let num = upper.replace(/[^0-9]/ig, "")
                let unit = upper.replace(num, "")
                return parseInt(num) * (map[unit] || 1)
            },

            //附件移除时，仅当非本次上传时触发remove事件
            remove(file) {
                //区分是否为本次新上传，以及是否上传成功
                //若不是新上传的
                if (!file.raw) this.$emit('remove', {...file, url: file.url.replace(attachmentPrefix, '')})
                //判断是否上传成功（有可能是正在上传）
                else if (file.response && !file.response.err) deleteUpload(file.raw.key)

                this.$refs.upload.handleRemove(file)
                let index = this.data.findIndex(i => i.url === file.url)
                if (index > -1) this.data.splice(index, 1)
            },

            //预览
            preview(file) {
                const index = this.data.findIndex(i => i.url === file.url)
                this.$image({index, urlList: this.previewUrlList})
            },

            //下载
            download(file) {
                download(file.url, file.name)
            },

            //数量超限
            exceed() {
                elError(`最多只能上传${this.limit}个文件`)
            },

            //新附件上传成功，触发success事件，记录文件key
            success(res, file) {
                file.raw.key = res.key
                this.data.push(file)
                this.$emit('success', file, res)
            },

            //上传失败时移除文件
            error(err, file) {
                elError('上传文件失败，' + err)
                file.response.err = err
                this.remove(file)
            },

            //上传前获取七牛的上传凭证，并指定key
            beforeUpload(file) {
                if (!file.type.includes('image')) {
                    elError('暂时只支持图片上传')
                    return false
                }
                const maxSize = this.parseMaxSize(this.maxSize)
                if (file.size > maxSize) {
                    elError(`${file.name}的大小超出${numberFormatter(maxSize)}`)
                    return false
                }
                /*return getToken()
                    .then(token => {
                        let now = new Date()
                        file.token = token
                        file.key = timeFormat('yyyy/MM/dd/', now) + now.getTime() + '/' + file.name
                    })*/
            },

            httpRequest(option) {
                const CancelToken = axios.CancelToken
                const source = CancelToken.source()
                const promise = upload(option.file, option.file.name, {
                    onUploadProgress(e) {
                        if (e.total > 0) {
                            e.percent = (Number)((e.loaded / e.total * 100).toFixed(2))
                        }
                        option.onProgress(e)
                    },
                    cancelToken: source.token
                })
                promise.abort = source.cancel
                return promise
            }
        }
    }
</script>
<style lang="scss">
    .disabled .el-upload--picture-card {
        display: none;
    }

    .el-upload-list--picture-card {
        .el-progress {
            width: 100%;
            height: 100%;

            .el-progress-circle {
                height: 100% !important;
                width: 100% !important;
            }
        }

        .progress-mask {
            position: absolute;
            top: 0;
            height: 100%;
            width: 100%;
            background-color: #fff;
            opacity: 0.9;

            .el-progress__text {
                color: $--color-primary;
            }
        }
    }

    .el-upload-list__item-thumbnail {
        object-fit: cover;
    }
</style>
