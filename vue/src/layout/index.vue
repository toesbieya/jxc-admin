<script type="text/jsx">
import offlineMixin from './mixin/offline'
import VAside from './component/Aside'
import VNavbar from './component/Navbar'
import VPage from './component/Page'
import {getters as appGetters} from "@/layout/store/app"
import {getters as pageGetters} from "@/layout/store/page"
import {getters as tagsViewGetters} from "@/layout/store/tagsView"

export default {
    name: 'Layout',

    mixins: [offlineMixin],

    components: {VAside, VNavbar, VPage},

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

    render() {
        const aside = this.renderAside && <v-aside/>

        return (
            <section class={this.wrapperClass}>
                {this.isLeftRight ? aside : <v-navbar/>}

                <section class={this.containerClass}>
                    {this.isLeftRight ? <v-navbar/> : aside}
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
