<template>
    <upload-card-list
        ref="upload"
        :uploaded-file-list="uploadedFileList"
        :limit="limit"
        :multiple="multiple"
        :disabled="disabled"
        :http-request="httpRequest"
        :before-upload="beforeUpload"
        @exceed="onExceed"
        @over-max-size="onOverMaxSize"
        @success="onSuccess"
        @error="onError"
        @preview="onPreview"
        @download="onDownload"
        @remove="onRemove"
    />
</template>

<script>
/*
* 直传七牛云
* 传入fileList自动拼接七牛云外链前缀
* 非图片类型的文件使用kkFileView预览
* success、remove事件中的file地址不带七牛云外链前缀
* */
import UploadCardList from "./CardList"
import axios from 'axios'
import {deleteUpload} from '@/api/file'
import {file as fileConfig} from '@/config'
import {elError} from "@/util/message"
import {isImage, isDoc, isPdf, isPpt, isRar, isXls, isTxt, isZip} from "@/util/validate"
import {preview, download, upload, autoCompleteUrl} from "@/util/file"

const typeMapper = [
    {test: isImage}, {test: isDoc, type: 'doc'}, {test: isPdf, type: 'pdf'}, {test: isPpt, type: 'ppt'},
    {test: isRar, type: 'rar'}, {test: isXls, type: 'xls'}, {test: isTxt, type: 'txt'}, {test: isZip, type: 'zip'},
]

function getCoverImage(url, fileType) {
    const mapper = typeMapper.find(({test}) => test(fileType))

    return mapper && mapper.type ? `${process.env.BASE_URL}static/img/fileType/${mapper.type}.png` : url
}

export default {
    name: "QiniuUpload",

    components: {UploadCardList},

    props: {
        disabled: Boolean,
        multiple: {type: Boolean, default: true},
        fileList: {type: Array, default: () => []},
        limit: {type: Number, default: 10},
        maxSize: {type: [Number, String], default: '10MB'}
    },

    computed: {
        //将{url, name}转为{thumbnailUrl, downloadUrl, name}的形式
        uploadedFileList() {
            return this.fileList.map(file => {
                const downloadUrl = autoCompleteUrl(file.url)
                return {...file, thumbnailUrl: getCoverImage(downloadUrl, file.name), downloadUrl}
            })
        }
    },

    methods: {
        //数量超限
        onExceed() {
            elError(`最多只能上传${this.limit}个文件`)
        },
        //文件大小超限
        onOverMaxSize(file) {
            const maxSize = this.$refs.upload.parseMaxSize(file.size)
            elError(`${file.name}的大小超出${this.$options.filters.number2StorageUnit(maxSize)}`)
        },
        //新附件上传成功，触发success事件，记录文件key
        onSuccess(res, file) {
            file.raw.key = res.key
            file.downloadUrl = autoCompleteUrl(res.key)
            this.$emit('success', file, res)
        },
        //上传失败
        onError(err) {
            elError('上传文件失败：' + err)
        },
        //预览
        onPreview(file) {
            if (!isImage(file.name)) return preview(file.downloadUrl)

            const fileList = this.$refs.upload.fileList
            const index = fileList.indexOf(file)
            const previewUrlList = fileList.filter(i => isImage(i.name)).map(i => i.downloadUrl)
            this.$image({index, urlList: previewUrlList})
        },
        //下载
        onDownload(file) {
            download(file.downloadUrl, file.name)
        },
        //移除
        onRemove(file) {
            if (file.status !== 'success') return

            //不是本次上传的
            if (!file.raw) {
                const key = file.downloadUrl.replace(fileConfig.storePrefix, '')
                return this.$emit('remove', {key, isNew: false})
            }

            //移除本次新上传的文件时，删除OSS上的文件
            deleteUpload.request(file.raw.key)
            this.$emit('remove', {key: file.raw.key, isNew: true})
        },

        //上传前过滤文件，并根据文件类型修改缩略图
        beforeUpload(file) {
            if (!typeMapper.some(({test}) => test(file.name))) {
                elError('只支持图片、word、pdf、ppt、rar、excel、txt、zip类型的文件')
                return false
            }

            const url = getCoverImage(file.thumbnailUrl, file.name)

            //修改非图片类型文件的缩略图
            if (url !== file.thumbnailUrl) {
                file.thumbnailUrl = url
            }

            return true
        },
        //覆盖默认的上传行为
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
            //取消上传时会调用一次abort方法
            promise.abort = source.cancel
            return promise
        }
    }
}
</script>
