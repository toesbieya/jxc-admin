<script type="text/jsx">
import VAside from './component/Aside'
import VNavbar from './component/Navbar'
import VPage from './component/Page'
import SettingDrawer from './component/SettingDrawer'
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
import {getters as pageGetters} from "@/layout/store/page"
import {getters as tagsViewGetters} from "@/layout/store/tagsView"

export default {
    name: 'Layout',

    components: {VAside, VNavbar, VPage, SettingDrawer},

    computed: {
        isLeftRight() {
            return pageGetters.position === 'left-right'
        },
        renderAside() {
            return appGetters.isMobile || ['aside', 'aside-two-part', 'mix'].includes(appGetters.navMode)
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
        }
    },

    methods: {
        closeSettingDrawer() {
            appMutations.showSettingDrawer(false)
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

                <setting-drawer
                    value={appGetters.showSettingDrawer}
                    on-input={this.closeSettingDrawer}
                />
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
    height: 100%;
    width: 100%;

    > .main-container {
        display: flex;
        flex: 1;
        overflow: hidden;
    }

    //左右结构时，侧边栏z-index需大于导航栏
    //上下结构时，侧边栏z-index需小于导航栏
    &.flex-column .aside {
        z-index: 9;
    }
}
</style>
