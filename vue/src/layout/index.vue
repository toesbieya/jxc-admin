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
import VMain from './component/Main'
import VHeader from './component/Header'
import VSidebar from './component/Sidebar'
import offlineMixin from "@/layout/mixin/offline"
import resizeMixin from './mixin/resize'

export default {
    name: 'Layout',

    mixins: [offlineMixin, resizeMixin],

    components: {VMain, VSidebar, VHeader},

    computed: {
        ...mapState('app', ['device', 'hasHeader']),

        ...mapState('setting', ['useTagsView', 'sidebarCollapse']),

        containerClass() {
            return {
                'el-container': true,
                'main-container': true,
                'has-header': this.hasHeader,
                'has-tags-view': this.useTagsView
            }
        },

        showSidebarMask() {
            return !this.sidebarCollapse && this.device === 'mobile'
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
