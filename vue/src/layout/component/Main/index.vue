<template>
    <main class="app-main">
        <div v-show="!showIframe" class="scroll-container">
            <breadcrumb v-if="showBreadcrumb"/>
            <page-view :include="cachedViews" :transition-name="transitionName"/>
            <page-footer/>
        </div>

        <!--跨域iframe无法调整高度，只能使用原生滚动条-->
        <iframe
            v-for="src in iframeList"
            v-show="showIframe && src === currentIframe"
            :id="src"
            :key="src"
            :src="src"
            frameborder="0"
            height="100%"
            width="100%"
        />

        <back-to-top :render="showBackToTop"/>
    </main>
</template>

<script>
import {getters as iframeGetters} from "@/layout/store/iframe"
import {getters as settingGetters} from "@/layout/store/setting"
import {getters as tagsViewGetters} from "@/layout/store/tagsView"
import BackToTop from "./component/BackToTop"
import Breadcrumb from './component/Breadcrumb'
import PageView from "./component/PageView"
import PageFooter from "./component/PageFooter"

export default {
    name: 'AppMain',

    components: {BackToTop, Breadcrumb, PageView, PageFooter},

    computed: {
        showBreadcrumb() {
            if (!settingGetters.showBreadcrumb) return false
            return this.$route.meta.breadcrumb !== false
        },
        showBackToTop: () => settingGetters.showBackToTop,

        cachedViews: () => tagsViewGetters.cachedViews,
        transitionName: () => tagsViewGetters.transitionName,

        showIframe: () => iframeGetters.show,
        currentIframe: () => iframeGetters.current,
        iframeList: () => iframeGetters.list,
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
