<template>
    <section class="app-wrapper">
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
import offlineMixin from './mixin/offline'
import resizeMixin from './mixin/resize'
import VMain from './component/Main'
import VHeader from './component/Header'
import VSidebar from './component/Sidebar'
import {getters as mainGetters} from "@/layout/store/main"
import {getters as settingGetters, mutations as settingMutations} from "@/layout/store/setting"

export default {
    name: 'Layout',

    mixins: [offlineMixin, resizeMixin],

    components: {VMain, VSidebar, VHeader},

    computed: {
        device: () => mainGetters.device,
        hasNav: () => mainGetters.hasNav,

        useTagsView: () => settingGetters.useTagsView,
        sidebarCollapse: () => settingGetters.sidebarCollapse,

        containerClass() {
            return {
                'main-container': true,
                'has-nav': this.hasNav,
                'has-tags-view': this.useTagsView
            }
        },

        showSidebarMask() {
            return !this.sidebarCollapse && this.device === 'mobile'
        }
    },

    methods: {
        collapseSidebar() {
            settingMutations.sidebarCollapse(true)
        }
    }
}
</script>

<style lang="scss">
.app-wrapper {
    display: flex;
    position: relative;
    height: 100%;
    width: 100%;

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
        display: flex;
        flex: 1;
        overflow: hidden;
        position: relative;
        flex-direction: column;
    }
}
</style>
