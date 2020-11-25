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
            return {'scroll-container': true, 'has-page-header': this.renderPageHeader}
        }
    },

    render() {
        const {showIframe, iframeList, currentIframe} = pageGetters
        const {cachedViews, transitionName, enabled} = tagsViewGetters

        return (
            <main class="page-main">
                {enabled && <tags-view/>}

                <div v-show={!showIframe} class={this.pageClass}>
                    {this.renderPageHeader && <page-header/>}

                    <page-view
                        include={cachedViews}
                        transition-name={transitionName}
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

<style lang="scss">
@import "~@/asset/style/variables.scss";

.page-main {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
    background-color: #f0f2f5;

    > .scroll-container {
        display: flex;
        flex: 1;
        flex-direction: column;
        overflow-y: overlay;
        overflow-x: inherit;

        > .page-view {
            margin: $page-view-margin;
            flex: 1;
        }
    }

    .back-to-top {
        height: 100%;
        width: 100%;
        background-color: #f2f5f6;
        box-shadow: 0 0 6px rgba(0, 0, 0, .12);
        text-align: center;
        line-height: 40px;
        color: #1989fa;
    }
}
</style>
