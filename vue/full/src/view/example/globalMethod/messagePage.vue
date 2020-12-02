<template>
    <div>
        <div class="tip-row">
            对element-ui的Message添加了仿<a href="https://quasar.dev/quasar-plugins/notify" target="_blank">Quasar</a>的进度条
        </div>

        <div>
            <el-button plain @click="message('success')">成功</el-button>
            <el-button plain @click="message('warning')">警告</el-button>
            <el-button plain @click="message('info')">消息</el-button>
            <el-button plain @click="message('error')">错误</el-button>
        </div>
    </div>
</template>

<script>
export default {
    name: "messagePage",

    methods: {
        message(type) {
            this.$message({message: '消息提示', type})
        }
    }
}
</script>
