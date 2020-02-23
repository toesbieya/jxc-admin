<template>
    <section class="el-container app-wrapper">
        <v-sidebar/>
        <section class="el-container main-container" :class="{'has-header':hasHeader}">
            <v-header/>
            <v-main/>
        </section>
    </section>
</template>

<script>
    import {mapState} from 'vuex'
    import VMain from './components/Main'
    import VHeader from './components/Header'
    import VSidebar from './components/Sidebar'
    import {isEmpty} from "@/utils"

    export default {
        name: 'Layout',
        components: {VMain, VSidebar, VHeader},
        computed: {
            ...mapState('app', {
                hasHeader: state => state.hasHeader
            }),
            ...mapState('socket', {
                online: state => state.online
            }),
            ...mapState('user', {
                isLogin: state => !isEmpty(state.id) && !isEmpty(state.token)
            }),
            showOfflineTip() {
                return !this.online && this.isLogin
            }
        },
        watch: {
            showOfflineTip(v) {
                v ? this.$bottomTip('与服务器失去连接') : this.$bottomTip.close()
            }
        }
    }
</script>

<style lang="scss">
    .app-wrapper {
        position: relative;
        height: 100%;
        width: 100%;
        flex-direction: row;
    }

    .main-container {
        overflow: hidden;
        position: relative;
        flex-direction: column;
    }
</style>
