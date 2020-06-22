<template>
    <section class="el-container app-wrapper">
        <v-sidebar/>

        <section :class="containerClass">
            <v-header/>
            <v-main/>
        </section>

        <!--移动端侧边栏展开时的遮罩-->
        <transition name="el-fade-in-linear">
            <div v-if="showSidebarMask" class="drawer-bg" @click.stop.prevent="collapseSidebar"/>
        </transition>
    </section>
</template>

<script>
    import {mapState} from 'vuex'
    import VMain from './components/Main'
    import VHeader from './components/Header'
    import VSidebar from './components/Sidebar'
    import ResizeHandler from './mixin/ResizeHandler'
    import {isEmpty} from "@/utils"

    export default {
        name: 'Layout',

        mixins: [ResizeHandler],

        components: {VMain, VSidebar, VHeader},

        computed: {
            ...mapState('app', {
                device: state => state.device,
                hasHeader: state => state.hasHeader
            }),

            ...mapState('setting', {
                useTagsView: state => state.useTagsView,
                sidebarCollapse: state => state.sidebarCollapse
            }),

            ...mapState('socket', {
                online: state => state.online
            }),

            ...mapState('user', {
                isLogin: state => !isEmpty(state.id) && !isEmpty(state.token)
            }),

            containerClass() {
                return {
                    'el-container': true,
                    'main-container': true,
                    'has-header': this.hasHeader,
                    'has-tags-view': this.useTagsView
                }
            },

            showOfflineTip() {
                return !this.online && this.isLogin
            },

            showSidebarMask() {
                return !this.sidebarCollapse && this.device === 'mobile'
            }
        },

        watch: {
            showOfflineTip(v) {
                v ? this.$bottomTip('与服务器失去连接') : this.$bottomTip.close()
            }
        },

        methods: {
            collapseSidebar() {
                this.$store.commit('setting/sidebarCollapse', true)
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

        .drawer-bg {
            background: rgba(0, 0, 0, .5);
            backdrop-filter: blur(10px);
            width: 100%;
            top: 0;
            height: 100%;
            position: fixed;
            z-index: 9;
        }

        .main-container {
            overflow: hidden;
            position: relative;
            flex-direction: column;
        }
    }
</style>
