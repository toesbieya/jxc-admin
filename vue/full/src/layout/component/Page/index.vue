<script type="text/jsx">
import {getters as pageGetters} from "@/layout/store/page"
import {getters as tagsViewGetters} from "@/layout/store/tagsView"
import BackToTop from "./component/BackToTop"
import PageHeader from "./component/PageHeader"
import PageView from "./component/PageView"
import PageFooter from "./component/PageFooter"
import TagsView from './component/TagsView'

export default {
    name: 'Page',

    components: {BackToTop, PageHeader, PageView, PageFooter, TagsView},

    computed: {
        renderPageHeader() {
            return pageGetters.showPageHeader && this.$route.meta.pageHeader !== false
        },
        pageClass() {
            return {
                'scroll-container': true,
                'has-page-header': this.renderPageHeader,
                'has-page-footer': true
            }
        }
    },

    render() {
        const {transition, showIframe, iframeList, currentIframe} = pageGetters
        const {cachedViews, enabled} = tagsViewGetters

        return (
            <main class="page-main">
                {enabled && <tags-view/>}

                <div v-show={!showIframe} class={this.pageClass}>
                    {this.renderPageHeader && <page-header/>}

                    <page-view
                        include={cachedViews}
                        transition-name={transition.curr}
                        cacheable={enabled}
                    />

                    <page-footer/>
                </div>

                {iframeList.map(src => (
                    <iframe
                        v-show={showIframe && src === currentIframe}
                        id={src}
                        key={src}
                        src={src}
                        frameborder="0"
                        height="100%"
                        width="100%"
                    />
                ))}

                {pageGetters.showBackToTop && <back-to-top/>}
            </main>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
