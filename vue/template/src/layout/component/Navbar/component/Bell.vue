<template>
    <el-badge :hidden="hidden" class="bell-badge" is-dot title="消息提醒" @click.native="jump">
        <i class="el-icon-bell navbar-icon"/>
    </el-badge>
</template>

<script>
import {refreshPage} from "@/util/route"

export default {
    name: "Bell",

    computed: {
        hidden() {
            return this.$store.state.message.unreadCount < 1
        }
    },

    methods: {
        jump() {
            const target = '/message/user'

            this.$route.path === target
                ? refreshPage()
                : this.$router.push(target)
        }
    },

    mounted() {
        this.$store.dispatch('message/refresh')
    }
}
</script>

<style>
.bell-badge .el-badge__content {
    top: 15px !important;
    right: 10px !important;
    border: none;
}
</style>
