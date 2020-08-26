<template>
    <div class="tip-row">
        测试接口返回速度<br>
        当前响应次数：{{ times }}，响应时间：{{ cur }}毫秒，平均响应：{{ avg }}毫秒，最长响应：{{ max }}毫秒
        <el-row>
            <el-button type="primary" @click="post">点我测试</el-button>
            <el-button plain @click="reset">重置</el-button>
        </el-row>
    </div>
</template>

<script>
import request, {GetApi, PostApi} from "@/api/request"

export default {
    name: "ApiSpeedTest",
    data() {
        return {
            loading: false,
            times: 0,
            cur: 0,
            avg: 0,
            max: 0,
            total: 0
        }
    },
    methods: {
        post() {
            if (this.loading) return
            this.loading = true
            const start = performance.now()
            request
                .post('/test/test')
                .finally(() => {
                    const end = (performance.now() - start)
                    if (end > this.max) this.max = end
                    this.cur = end.toFixed(2)
                    this.total += end
                    this.times++
                    this.avg = (this.total / this.times).toFixed(2)
                    this.loading = false
                })
        },

        reset() {
            if (this.loading) return
            this.times = 0
            this.cur = 0
            this.avg = 0
            this.max = 0
            this.total = 0
        }
    }
}
</script>
