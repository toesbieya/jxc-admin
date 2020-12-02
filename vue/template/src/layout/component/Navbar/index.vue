<script type="jsx">
import {mapState} from 'vuex'
import hamburgerMixin from '@/layout/mixin/hamburger'
import HeadMenu from "./component/HeadMenu"
import Logo from "@/layout/component/Logo"
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
import {getters as navbarGetters} from "@/layout/store/navbar"
import {getters as pageGetters} from "@/layout/store/page"
import {elConfirm} from "@/util/message"
import {refreshPage} from "@/util/route"

export default {
    name: 'navbar',

    mixins: [hamburgerMixin],

    components: {HeadMenu, Logo},

    data() {
        return {
            guideSteps: [
                {
                    element: '.setting-btn.navbar-item',
                    content: '这是个性设置按钮，可以根据自己喜好进行一些设置',
                },
                {
                    element: '.navbar .el-dropdown.navbar-item',
                    content: '这是用户中心',
                },
                {
                    element: '.tags-view-container',
                    content: `<p>这是tab栏，可以右键tab页进行相关操作</p>
                              <p>ctrl + ← → 可以进行tab页的左右切换</p>
                              <p>当tab过多时通过鼠标滚轮来滚动</p>
                              <p>双击可以关闭</p>`,
                },
            ]
        }
    },

    computed: {
        ...mapState('user', ['avatar', 'name', 'prepareLogout']),

        //渲染导航栏logo的条件
        //①桌面端
        //②设置了显示logo
        //③导航模式为顶部导航或页面为上下结构
        renderLogo() {
            return !appGetters.isMobile
                && pageGetters.showLogo
                && (appGetters.navMode === 'head' || pageGetters.position === 'top-bottom')
        },

        //渲染顶部导航菜单的条件
        //①桌面端
        //②导航模式为顶部导航或混合导航
        renderHeadMenu() {
            return !appGetters.isMobile && ['head', 'mix'].includes(appGetters.navMode)
        },

        className() {
            return `navbar ${navbarGetters.theme}`
        }
    },

    methods: {
        command(command) {
            switch (command) {
                case 'user':
                    const path = '/user'
                    this.$route.path !== path && this.$router.push(path)
                    break
                case 'guide':
                    this.$guide(this.guideSteps)
                    break
                case 'logout':
                    if (!this.prepareLogout) {
                        elConfirm('确认退出?')
                            .then(() => this.$store.dispatch('user/logout'))
                    }
                    break
            }
        },

        clickRefreshBtn() {
            refreshPage()
        },

        openSettingDrawer() {
            appMutations.showSettingDrawer(true)
        }
    },

    render() {
        return (
            <nav class={this.className}>
                {this.renderLogo && <logo show-title/>}

                {this.renderHamburger && <hamburger class="navbar-item navbar-icon"/>}

                <div style="flex: 1">
                    {this.renderHeadMenu && <head-menu always-show theme={navbarGetters.theme}/>}
                </div>

                <div>
                    <div
                        class="navbar-item"
                        title="刷新"
                        on-click={this.clickRefreshBtn}
                    >
                        <i class="el-icon-refresh-right navbar-icon"/>
                    </div>

                    <div
                        class="setting-btn navbar-item"
                        title="个性设置"
                        on-click={this.openSettingDrawer}
                    >
                        <i class="el-icon-s-operation navbar-icon"/>
                    </div>

                    <el-dropdown class="navbar-item" on-command={this.command}>
                        <div class="avatar-wrapper">
                            <el-avatar size={30} src={this.avatar} icon="el-icon-user-solid"/>
                            <span class="hidden-xs">{this.name}</span>
                        </div>
                        <el-dropdown-menu
                            slot="dropdown"
                            class={navbarGetters.theme}
                            visible-arrow={false}
                        >
                            <el-dropdown-item icon="el-icon-user" command="user">
                                个人中心
                            </el-dropdown-item>
                            <el-dropdown-item class="hidden-xs" icon="el-icon-guide" command="guide">
                                新手指引
                            </el-dropdown-item>
                            <el-dropdown-item icon="el-icon-switch-button" command="logout">
                                退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>
            </nav>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
