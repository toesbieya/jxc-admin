<template>
    <div>
        <div class="tip-row">
            拼图验证，
            在<a href="https://github.com/Kevin-269581661/vue-puzzle-verification" target="_blank"></a>的基础上做了样式修改
        </div>
        <el-button type="primary" @click="onClick">触发验证</el-button>
    </div>
</template>

<script>
import {elSuccess, elError} from "@/util/message"

export default {
    name: "verifyPage",

    methods: {
        onClick() {
            this.$verify()
                .then(() => elSuccess('验证通过！'))
                .catch(() => elError('验证失败！'))
        }
    }
}
</script>
