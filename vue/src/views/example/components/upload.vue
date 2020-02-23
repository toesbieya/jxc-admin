<template>
    <div>
        <div class="tip-row">根据业务需求简单封装了upload，集成自带预览组件</div>
        <upload-file/>
        <div class="tip-row">原始使用axios的上传</div>
        <input @change="change" type="file">
    </div>
</template>

<script>
    import UploadFile from '@/bizComponents/UploadFile'
    import request from '@/config/request'

    export default {
        name: "upload",
        components: {UploadFile},
        methods: {
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
                request.post('/test/upload', param, {headers: {"Content-Type": "multipart/form-data"}})
                    .then(({data}) => alert(data))
            }
        }
    }
</script>
