<template>
    <div>
        <div class="tip-row">
            根据业务需求简单封装了upload<br/>
            图片使用<a href="https://github.com/sachinchoolur/lightgallery.js" target="_blank">lightgallery</a>预览，
            非图片类型的文件使用<a href="https://github.com/kekingcn/kkFileView" target="_blank">kkFileView</a>预览
        </div>
        <qiniu-upload :file-list="fileList" @success="success" @remove="remove"/>

        <div class="tip-row">原始使用axios的上传</div>
        <input @change="change" type="file">
    </div>
</template>

<script>
import QiniuUpload from "@/component/Upload/Qiniu"
import request from "@/api/request"
import {deleteUploadBatch} from "@/api/file"

export default {
    name: "uploadExample",

    components: {QiniuUpload},

    data() {
        return {
            fileList: [
                {
                    url: 'https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=4018557288,1217151095&fm=26&gp=0.jpg',
                    name: '1jpgjpgjpgjpgjpgjp1jpgjpgjpgjpgjpgjpgjpggjpg.jpg'
                },
                {
                    url: 'https://dss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3252521864,872614242&fm=26&gp=0.jpg',
                    name: '2.jpg'
                },
                {
                    url: 'https://dss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2534506313,1688529724&fm=26&gp=0.jpg',
                    name: '3.jpg'
                },
            ],
            uploadedKeys: []
        }
    },

    methods: {
        success(file, res) {
            this.uploadedKeys.push(res.key)
        },
        remove({key, isNew}) {
            if (!isNew) return

            const index = this.uploadedKeys.findIndex(i => i.url === key)
            index > -1 && this.uploadedKeys.splice(index, 1)
        },

        change(e) {
            let file = e.target.files[0]
            let param = new FormData()

            let selfFilename = '这是自己另外取得文件名'

            //这样的文件名称就是selfFilename
            param.append('file', file, selfFilename)

            //这样的文件名称就是上传时的名称
            //param.append('file', file)

            //也可以用RequestParam根据键名去取额外参数
            param.append('extraParam1', "额外参数1")
            param.append('extraParam2', "额外参数2")
            request
                .post('/test/upload', param, {headers: {"Content-Type": "multipart/form-data"}})
                .then(({data}) => alert(data))
        }
    },

    beforeDestroy() {
        if (this.uploadedKeys.length > 0) {
            deleteUploadBatch.request(this.uploadedKeys)
        }
    }
}
</script>
