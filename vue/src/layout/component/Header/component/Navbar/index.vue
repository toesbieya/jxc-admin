<script type="jsx">
import {mapState} from 'vuex'
import GuideMixin from '@/mixin/guide'
import Bell from "./component/Bell"
import Hamburger from './component/Hamburger'
import HeadMenu from "./component/HeadMenu"
import SettingDrawer from './component/SettingDrawer'
import {getters as appGetters} from "@/layout/store/app"
import {getters as asideGetters, mutations as asideMutations} from "@/layout/store/aside"
import {getSidebarMenus} from "@/layout/util"
import {elConfirm} from "@/util/message"

export default {
    name: 'navbar',

    mixins: [GuideMixin.navbar],

    components: {Hamburger, Bell, HeadMenu, SettingDrawer},

    data: () => ({settingDrawer: false}),

    computed: mapState('user', ['avatar', 'name', 'prepareLogout']),

    methods: {
        refresh() {
            this.$router.replace({path: `/redirect${this.$route.fullPath}`})
        },

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

        renderHamburger() {
            //渲染折叠按钮的条件
            //①侧边栏有菜单
            //②移动端
            //③桌面端且非顶部导航
            //④桌面端且未设置侧边栏自动隐藏
            const hasSidebarMenus = getSidebarMenus().length > 0,
                isMobile = appGetters.isMobile,
                notHeadMode = appGetters.navMode !== 'head',
                notAutoHidden = !asideGetters.autoHide,
                renderHamburger = hasSidebarMenus && (isMobile || notHeadMode && notAutoHidden)

            if (renderHamburger) {
                const active = isMobile ? asideGetters.show : !asideGetters.collapse
                return <hamburger class="navbar-item" active={active} on-click={() => asideMutations.switch()}/>
            }
        },
        renderHeadMenuMenu() {
            if (appGetters.navMode !== 'aside' && !appGetters.isMobile) {
                return <head-menu always-show/>
            }
        },
        renderActionButtons() {
            return [
                <bell class="navbar-item"/>,

                <div class="navbar-item" title="刷新" on-click={this.refresh}>
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
                <div style="flex: 0.5">
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

<style lang="scss" src="./style.scss"></style>
