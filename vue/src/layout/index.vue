<script type="text/jsx">
import offlineMixin from './mixin/offline'
import VAside from './component/Aside'
import VHeader from './component/Header'
import VPage from './component/Page'
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
import {getters as tagsViewGetters} from "@/layout/store/tagsView"
import {debounce} from "@/util"
import {isMobile} from "@/util/browser"

export default {
    name: 'Layout',

    mixins: [offlineMixin],

    components: {VAside, VHeader, VPage},

    computed: {
        containerClass() {
            const arr = ['main-container', 'has-nav', `nav-mode-${appGetters.navMode}`]
            tagsViewGetters.enabled && arr.push('has-tags-view')
            return arr
        }
    },

    methods: {
        $_resizeHandler() {
            if (!document.hidden) {
                appMutations.isMobile(isMobile())
            }
        }
    },

    mounted() {
        this.$_resizeHandler = debounce(this.$_resizeHandler)
        this.$_resizeHandler()

        window.addEventListener('resize', this.$_resizeHandler)

        this.$once('hook:beforeDestroy', () => {
            window.removeEventListener('resize', this.$_resizeHandler)
        })
    },

    render() {
        const renderAide = appGetters.isMobile || appGetters.navMode !== 'head'
        return (
            <section class="app-wrapper">
                {renderAide && <v-aside/>}

                <section class={this.containerClass}>
                    <v-header/>
                    <v-page/>
                </section>
            </section>
        )
    }
}
</script>

<style lang="scss">
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
        flex-direction: column;
    }
}
</style>
