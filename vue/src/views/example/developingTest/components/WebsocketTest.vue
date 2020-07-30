<template>
    <div class="tip-row">
        测试socket连接
        <el-row>
            <el-input v-model="address" placeholder="socket服务地址"/>
            <el-input v-model="option" placeholder="socket.io参数" :rows="4" type="textarea" style="margin-top: 12px"/>
        </el-row>
        <el-row style="margin-top: 12px">
            连接情况：{{ result }}
            <el-button :loading="loading" type="primary" @click="connect">点我连接</el-button>
            <el-button plain @click="stop">中止</el-button>
        </el-row>
    </div>
</template>

<script>
import SocketIO from 'socket.io-client'

export default {
    name: "WebsocketTest",

    data() {
        return {
            loading: false,
            result: '',
            address: '',
            option: ''
        }
    },

    methods: {
        connect() {
            if (this.loading || !this.address) return
            this.socket = new SocketIO(this.address, this.getSocketOption())
            this.socket.on('connect', () => {
                this.result = '连接成功'
                this.stop()
                this.loading = false
            })
            this.socket.on('reconnecting', () => {
                this.result = '连接失败'
                this.stop()
                this.loading = false
            })
        },

        stop() {
            if (!this.loading || !this.socket) return
            this.socket.close()
            this.socket = null
        },

        getSocketOption() {
            try {
                return JSON.parse(this.option)
            }
            catch (e) {
                return {}
            }
        }
    }
}
</script>
