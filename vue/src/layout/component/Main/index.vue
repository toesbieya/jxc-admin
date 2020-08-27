<template>
    <main class="app-main">
        <div v-show="!showIframe" class="scroll-container">
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
import {mapState} from 'vuex'
import BackToTop from "./component/BackToTop"
import PageView from "./component/PageView"
import PageFooter from "./component/PageFooter"

export default {
    name: 'AppMain',

    components: {BackToTop, PageView, PageFooter},

    computed: {
        ...mapState('setting', ['showBackToTop']),

        ...mapState('tagsView', ['cachedViews', 'transitionName']),

        ...mapState('iframe', {
            showIframe: state => state.show,
            currentIframe: state => state.current,
            iframeList: state => state.list
        })
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
