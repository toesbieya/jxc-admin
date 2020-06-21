<template>
    <el-badge :hidden="hidden" class="bell-badge" is-dot @click.native="jump">
        <i class="el-icon-bell navbar-icon" title="消息提醒"/>
    </el-badge>
</template>

<script>
    export default {
        name: "Bell",

        computed: {
            hidden() {
                return this.$store.state.message.unreadCount < 1
            }
        },

        methods: {
            jump() {
                let target = '/message/user'
                if (this.$route.path === target) target = '/redirect' + target
                this.$router.replace(target)
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
    }
</style>
