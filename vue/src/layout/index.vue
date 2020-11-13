<script type="text/jsx">
import offlineMixin from './mixin/offline'
import VAside from './component/Aside'
import VHeader from './component/Header'
import VPage from './component/Page'
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
import {getters as pageGetters} from "@/layout/store/page"
import {getters as tagsViewGetters} from "@/layout/store/tagsView"
import {debounce} from "@/util"
import {isMobile} from "@/util/browser"

export default {
    name: 'Layout',

    mixins: [offlineMixin],

    components: {VAside, VHeader, VPage},

    computed: {
        isLeftRight() {
            return pageGetters.position === 'left-right'
        },
        wrapperClass() {
            return {
                'app-wrapper': true,
                'flex-column': !this.isLeftRight
            }
        },
        containerClass() {
            return [
                'main-container',
                'has-nav',
                `nav-mode-${appGetters.navMode}`,
                this.isLeftRight && 'flex-column',
                tagsViewGetters.enabled && 'has-tags-view'
            ]
        },
        renderAside() {
            return appGetters.isMobile || ['aside', 'aside-two-part', 'mix'].includes(appGetters.navMode)
        }
    },

    mounted() {
        this.$_resizeHandler = debounce(() => {
            !document.hidden && appMutations.isMobile(isMobile())
        })
        this.$_resizeHandler()

        window.addEventListener('resize', this.$_resizeHandler)

        this.$once('hook:beforeDestroy', () => {
            window.removeEventListener('resize', this.$_resizeHandler)
        })
    },

    render() {
        return (
            <section class={this.wrapperClass}>
                {this.isLeftRight ? this.renderAside && <v-aside/> : <v-header/>}

                <section class={this.containerClass}>
                    {this.isLeftRight ? <v-header/> : <v-aside/>}
                    <v-page/>
                </section>
            </section>
        )
    }
}
</script>

<style lang="scss">
.app-wrapper.flex-column,
.main-container.flex-column {
    flex-direction: column;
}

.app-wrapper {
    display: flex;
    position: relative;
    height: 100%;
    width: 100%;

    > .main-container {
        display: flex;
        flex: 1;
        overflow: hidden;
        position: relative;
    }
}
</style>
