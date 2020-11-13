<script type="jsx">
import {mapState} from 'vuex'
import GuideMixin from '@/mixin/guide'
import Bell from "./component/Bell"
import Hamburger from './component/Hamburger'
import HeadMenu from "./component/HeadMenu"
import Logo from "@/layout/component/Logo"
import SettingDrawer from '@/layout/component/SettingDrawer'
import {getters as appGetters} from "@/layout/store/app"
import {getters as asideGetters, mutations as asideMutations} from "@/layout/store/aside"
import {getters as pageGetters} from "@/layout/store/page"
import {getSidebarMenus} from "@/layout/util"
import {elConfirm} from "@/util/message"
import {refreshPage} from "@/util/route"

export default {
    name: 'navbar',

    mixins: [GuideMixin.navbar],

    components: {Bell, Hamburger, HeadMenu, Logo, SettingDrawer},

    data: () => ({settingDrawer: false}),

    computed: mapState('user', ['avatar', 'name', 'prepareLogout']),

    methods: {
        command(command) {
            switch (command) {
                case 'user':
                    this.$router.push('/user')
                    break
                case 'guide':
                    this.$guide(0, this.guideSteps)
                    break
                case 'logout':
                    if (this.prepareLogout) return
                    elConfirm('确认退出?').then(() => this.$store.dispatch('user/logout'))
                    break
            }
        },

        renderLogo() {
            const render =
                !appGetters.isMobile
                && pageGetters.showLogo
                && pageGetters.position === 'top-bottom'

            if (render) return <logo show-title/>
        },
        renderHamburger() {
            //渲染折叠按钮的条件
            //①侧边栏有菜单
            //②移动端
            //③桌面端是双层侧边栏导航
            //④桌面端且是侧边栏导航或混合导航时，未设置侧边栏自动隐藏
            const hasSidebarMenus = getSidebarMenus().length > 0,
                isMobile = appGetters.isMobile,
                correctMode =
                    ['aside', 'mix'].includes(appGetters.navMode) && !asideGetters.autoHide
                    || appGetters.navMode === 'aside-two-part',
                renderHamburger = hasSidebarMenus && (isMobile || correctMode)

            if (renderHamburger) {
                const active = isMobile ? asideGetters.show : !asideGetters.collapse
                return <hamburger class="navbar-item" active={active} on-click={() => asideMutations.switch()}/>
            }
        },
        renderHeadMenuMenu() {
            if (['head', 'mix'].includes(appGetters.navMode) && !appGetters.isMobile) {
                return <head-menu always-show/>
            }
        },
        renderActionButtons() {
            return [
                <bell class="navbar-item"/>,

                <div class="navbar-item" title="刷新" on-click={() => refreshPage()}>
                    <i class="el-icon-refresh-right navbar-icon"/>
                </div>,

                <div class="setting-btn navbar-item"
                     title="个性设置"
                     on-click={() => this.settingDrawer = true}
                >
                    <i class="el-icon-s-operation navbar-icon"/>
                </div>
            ]
        },
        renderUserButton() {
            return (
                <el-dropdown
                    class="navbar-item"
                    trigger="click"
                    on-command={this.command}
                >
                    <div class="avatar-wrapper">
                        <el-avatar size={30} src={this.avatar} icon="el-icon-user-solid"/>
                        <span class="hidden-xs">{this.name}</span>
                    </div>
                    <el-dropdown-menu class="user-dropdown" slot="dropdown">
                        <el-dropdown-item icon="el-icon-user" command="user">
                            个人中心
                        </el-dropdown-item>
                        <el-dropdown-item class="hidden-xs" icon="el-icon-guide" command="guide">
                            新手指引
                        </el-dropdown-item>
                        <el-dropdown-item divided icon="el-icon-switch-button" command="logout">
                            退出登录
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            )
        }
    },

    render() {
        return (
            <nav class="navbar">
                {this.renderLogo()}

                <div style="flex: 1">
                    {this.renderHamburger()}
                    {this.renderHeadMenuMenu()}
                </div>

                <div>
                    {this.renderActionButtons()}
                    {this.renderUserButton()}
                </div>

                <setting-drawer v-model={this.settingDrawer}/>
            </nav>
        )
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: nowrap;
    height: $nav-height;
    padding-left: 12px;
    transition: height .3s ease-in-out;
    position: relative;
    z-index: 9;
    background-color: #ffffff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

    > div {
        display: flex;
        flex-wrap: nowrap;
        height: 100%;
    }

    .navbar-icon {
        font-size: 18px;
    }

    .navbar-item {
        display: flex;
        align-items: center;
        padding: 0 8px;
        color: #5a5e66;
        transition: background .3s;
        cursor: pointer;

        &:hover {
            background: rgba(0, 0, 0, .025)
        }
    }

    .logo-container {
        width: auto;
        min-width: 192px;
        padding-left: 0;

        > .logo-link > h1 {
            font-weight: 400;
            color: #303133;
            font-size: 16px;
        }
    }

    .avatar-wrapper {
        display: flex;
        align-items: center;

        span {
            margin-right: 5px;
            font-size: 18px;
        }
    }
}
</style>
