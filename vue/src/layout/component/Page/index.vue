<script type="text/jsx">
import {getters as pageGetters} from "@/layout/store/page"
import {getters as tagsViewGetters} from "@/layout/store/tagsView"
import BackToTop from "./component/BackToTop"
import PageHeader from "./component/PageHeader"
import PageView from "./component/PageView"
import PageFooter from "./component/PageFooter"

export default {
    name: 'Page',

    components: {BackToTop, PageHeader, PageView, PageFooter},

    methods: {
        renderPage() {
            const render = pageGetters.showPageHeader && this.$route.meta.pageHeader !== false
            const className = {'scroll-container': true, 'has-page-header': render}
            const {cachedViews, transitionName} = tagsViewGetters

            return (
                <div v-show={!pageGetters.showIframe} class={className}>
                    {render && <page-header/>}
                    <page-view include={cachedViews} transition-name={transitionName}/>
                    <page-footer/>
                </div>
            )
        },
        renderIframe() {
            const {showIframe, iframeList, currentIframe} = pageGetters

            return iframeList.map(src => (
                <iframe
                    v-show={showIframe && src === currentIframe}
                    id={src}
                    key={src}
                    src={src}
                    frameborder="0"
                    height="100%"
                    width="100%"
                />
            ))
        }
    },

    render() {
        return (
            <main class="router-main">
                {this.renderPage()}
                {this.renderIframe()}
                {pageGetters.showBackToTop && <back-to-top/>}
            </main>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
