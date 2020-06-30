<template>
    <el-upload
            ref="upload"
            :before-upload="beforeUpload"
            :class="{disabled: hideUploader}"
            :file-list="tempData"
            :http-request="httpRequest"
            :limit="limit"
            :multiple="multiple"
            :on-error="error"
            :on-exceed="exceed"
            :on-success="success"
            action=""
            list-type="picture-card"
    >
        <i class="el-icon-plus"/>

        <template v-slot:file="{file}">
            <span
                    class="el-upload-list__item-actions"
                    @mouseenter="e => handleBlockMouseEnter(e,file)"
                    @mouseleave="handleBlockMouseLeave"
            >
                <span class="el-upload-list__item-preview" @click="() => preview(file)">
                    <i class="el-icon-zoom-in"/>
                </span>
                <span class="el-upload-list__item-delete" @click="() => download(file)">
                    <i class="el-icon-download"/>
                </span>
                <span v-if="!disabled" class="el-upload-list__item-delete" @click="() => remove(file)">
                    <i class="el-icon-delete"/>
                </span>
            </span>

            <template v-if="file.status === 'success'">
                <img :src="file.url" class="el-upload-list__item-thumbnail">
                <label class="el-upload-list__item-status-label">
                    <i class="el-icon-upload-success el-icon-check"/>
                </label>
            </template>

            <div v-if="file.status === 'uploading'" class="progress-mask">
                <el-progress :percentage="file.percentage" type="circle"/>
            </div>
        </template>

        <el-tooltip ref="tooltip" :content="tooltipContent" popper-class="upload-tooltip"/>
    </el-upload>
</template>

<script>
    /*
    * 直传七牛云
    * 传入fileList自动拼接七牛云外链前缀，并增加downloadUrl属性（图片类型的文件url与downloadUrl相同）
    * 非图片类型的文件使用kkFileView预览
    * success、remove事件中的file.url均不带七牛云外链前缀
    * */
    import axios from 'axios'
    import {attachmentPrefix} from '@/config'
    import {numberFormatter} from "@/filter"
    import {debounce, isEmpty} from '@/utils'
    import {elError} from "@/utils/message"
    import {isImage, isDoc, isPdf, isPpt, isRar, isXls, isTxt, isZip} from "@/utils/validate"
    import {preview, deleteUpload, download, upload, autoCompleteUrl} from "@/utils/file"
    import {getContextPath} from "@/utils/browser"

    const typeMapper = [
        {test: isImage}, {test: isDoc, type: 'doc'}, {test: isPdf, type: 'pdf'}, {test: isPpt, type: 'ppt'},
        {test: isRar, type: 'rar'}, {test: isXls, type: 'xls'}, {test: isTxt, type: 'txt'}, {test: isZip, type: 'zip'},
    ]

    function parseMaxSize(maxSize) {
        if (typeof maxSize === 'number') {
            return maxSize
        }

        const map = {KB: 1024, MB: 1024 * 1024},
            upper = maxSize.toUpperCase(),
            num = upper.replace(/[^0-9]/ig, ""),
            unit = upper.replace(num, "")

        return parseInt(num) * (map[unit] || 1)
    }

    function getCoverImage(url, fileType) {
        const mapper = typeMapper.find(({test}) => test(fileType))

        return mapper && mapper.type ? `${getContextPath()}static/img/fileType/${mapper.type}.png` : url
    }

    export default {
        name: 'UploadFile',

        props: {
            disabled: Boolean,
            multiple: {type: Boolean, default: true},
            fileList: {type: Array, default: () => []},
            limit: {type: Number, default: 10},
            maxSize: {type: Number | String, default: '10MB'}
        },

        data() {
            return {
                tempData: this.fileList,
                data: this.fileList,
                tooltipContent: null
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
                return this.data ? this.data.map(i => i.downloadUrl) : []
            }
        },

        watch: {
            fileList: {
                immediate: true,
                handler(fileList) {
                    if (isEmpty(fileList)) this.data = []
                    else {
                        this.data = [...this.fileList.map(file => {
                            const downloadUrl = autoCompleteUrl(file.url)
                            return {...file, url: getCoverImage(downloadUrl, file.name), downloadUrl}
                        })]
                    }
                    this.tempData = JSON.parse(JSON.stringify(this.data))
                }
            }
        },

        methods: {
            //模仿el-table的tooltip
            handleBlockMouseEnter(event, file) {
                const tooltip = this.$refs.tooltip
                this.tooltipContent = file.name
                tooltip.referenceElm = event.target
                tooltip.$refs.popper && (tooltip.$refs.popper.style.display = 'none')
                tooltip.doDestroy()
                tooltip.setExpectedState(true)
                this.activateTooltip(tooltip)
            },
            handleBlockMouseLeave() {
                const tooltip = this.$refs.tooltip
                if (tooltip) {
                    tooltip.setExpectedState(false)
                    tooltip.handleClosePopper()
                }
            },

            //附件移除时，仅当非本次上传时触发remove事件
            remove(file) {
                //删除上传文件时隐藏tooltip
                this.handleBlockMouseLeave()

                //区分是否为本次新上传，以及是否上传成功
                //若不是新上传的
                if (!file.raw) this.$emit('remove', {url: file.downloadUrl.replace(attachmentPrefix, '')})
                //判断是否上传成功（有可能是正在上传）
                else if (file.response && !file.response.err) deleteUpload(file.raw.key)

                this.$refs.upload.handleRemove(file)
                const index = this.data.findIndex(i => i.downloadUrl === file.downloadUrl)
                if (index > -1) this.data.splice(index, 1)
            },

            //预览
            preview(file) {
                if (!isImage(file.name)) return preview(file.downloadUrl)

                const index = this.data.findIndex(i => i.downloadUrl === file.downloadUrl)
                this.$image({index, urlList: this.previewUrlList})
            },

            //下载
            download(file) {
                download(file.downloadUrl, file.name)
            },

            //数量超限
            exceed() {
                elError(`最多只能上传${this.limit}个文件`)
            },

            //新附件上传成功，触发success事件，记录文件key
            success(res, file) {
                file.url = getCoverImage(file.url, file.name)
                file.raw.key = res.key
                file.downloadUrl = autoCompleteUrl(res.key)
                this.data.push(file)
                this.$emit('success', file, res)
            },

            //上传失败时移除文件
            error(err, file) {
                elError('上传文件失败，' + err)
                file.response.err = err
                this.remove(file)
            },

            //上传前过滤文件
            beforeUpload(file) {
                if (!typeMapper.some(({test}) => test(file.name))) {
                    elError('只支持图片、word、pdf、ppt、rar、excel、txt、zip类型的文件')
                    return false
                }
                const maxSize = parseMaxSize(this.maxSize)
                if (file.size > maxSize) {
                    elError(`${file.name}的大小超出${numberFormatter(maxSize)}`)
                    return false
                }
            },

            httpRequest({file, onProgress}) {
                const source = axios.CancelToken.source()
                const promise = upload(file, file.name, {
                    onUploadProgress(e) {
                        if (e.total > 0) {
                            e.percent = (Number)((e.loaded / e.total * 100).toFixed(2))
                        }
                        onProgress(e)
                    },
                    cancelToken: source.token
                })
                //由于ele原有的上传方法是原生ajax，所以取消上传时会调用一次abort方法
                promise.abort = source.cancel
                return promise
            }
        },

        created() {
            this.activateTooltip = debounce(tooltip => tooltip.handleShowPopper(), 200)
        }
    }
</script>

<style lang="scss" src="./style.scss"></style>
