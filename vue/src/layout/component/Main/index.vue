<script type="text/jsx">
import {getters as iframeGetters} from "@/layout/store/iframe"
import {getters as settingGetters} from "@/layout/store/setting"
import {getters as tagsViewGetters} from "@/layout/store/tagsView"
import BackToTop from "./component/BackToTop"
import PageHeader from "./component/PageHeader"
import PageView from "./component/PageView"
import PageFooter from "./component/PageFooter"

export default {
    name: 'AppMain',

    components: {BackToTop, PageHeader, PageView, PageFooter},

    computed: {
        showPageHeader() {
            if (!settingGetters.showPageHeader) return false
            return this.$route.meta.pageHeader !== false
        },
        showBackToTop: () => settingGetters.showBackToTop,

        cachedViews: () => tagsViewGetters.cachedViews,
        transitionName: () => tagsViewGetters.transitionName,

        showIframe: () => iframeGetters.show,
        currentIframe: () => iframeGetters.current,
        iframeList: () => iframeGetters.list,
    },

    methods: {
        renderPage() {
            return (
                <div v-show={!this.showIframe} class="scroll-container">
                    {this.showPageHeader && <page-header/>}
                    <page-view include={this.cachedViews} transition-name={this.transitionName}/>
                    <page-footer/>
                </div>
            )
        },
        renderIframe() {
            return this.iframeList.map(src => (
                <iframe
                    v-show={this.showIframe && src === this.currentIframe}
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
                {this.showBackToTop && <back-to-top/>}
            </main>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
