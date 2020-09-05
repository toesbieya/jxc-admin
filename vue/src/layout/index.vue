<script type="text/jsx">
import offlineMixin from './mixin/offline'
import VAside from './component/Aside'
import VHeader from './component/Header'
import VMain from './component/Main'
import {getters as mainGetters, mutations as mainMutations} from "@/layout/store/main"
import {getters as settingGetters} from "@/layout/store/setting"
import {isMobile} from "@/util/browser"
import {debounce} from "@/util"

export default {
    name: 'Layout',

    mixins: [offlineMixin],

    components: {VAside, VHeader, VMain},

    computed: {
        containerClass() {
            const arr = ['main-container', 'has-nav', `nav-mode-${settingGetters.navMode}`]
            settingGetters.useTagsView && arr.push('has-tags-view')
            return arr
        }
    },

    methods: {
        $_resizeHandler() {
            if (!document.hidden) {
                const mobile = isMobile()
                mainMutations.device(mobile ? 'mobile' : 'pc')
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
        const renderAide = mainGetters.device === 'mobile' || settingGetters.navMode !== 'head'
        return (
            <section class="app-wrapper">
                {renderAide && <v-aside/>}

                <section class={this.containerClass}>
                    <v-header/>
                    <v-main/>
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
