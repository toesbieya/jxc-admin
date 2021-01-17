<template>
    <el-admin-layout :navbar-props="navbarProps" :page-props="pageProps"/>
</template>

<script type="text/jsx">
import Vue from 'vue'
import config from "@/config"
import ElAdminLayout, {Const, appMutations} from 'el-admin-layout'
import Footer from './component/Footer'
import NotifyBell from './component/NotifyBell'
import SettingDrawer from './component/SettingDrawer'
import offlineMixin from './mixin/offline'
import {elConfirm} from "@/util/message"

//设置一些基础信息
appMutations.title(config.title)
appMutations.logo(config.logo)

//设置图标渲染方式
Const.iconRenderer = (h, icon) => <v-icon icon={icon}/>

export default {
    name: "Layout",

    mixins: [offlineMixin],

    components: {ElAdminLayout, NotifyBell, SettingDrawer},

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
        navbarProps() {
            const userInfo = this.$store.state.user
            return {
                avatar: userInfo.avatar,
                username: userInfo.name,
                userDropdownItems: [
                    {
                        icon: 'el-icon-user',
                        command: 'user-center',
                        content: '用户中心',
                        handler: this.toUserCenter
                    },
                    {
                        icon: 'el-icon-guide',
                        command: 'guide',
                        content: '新手指引',
                        hideOnMobile: true,
                        handler: this.startGuide
                    },
                    {
                        icon: 'el-icon-switch-button',
                        command: 'logout',
                        content: '退出登录',
                        handler: this.logout
                    }
                ],
                renderCustomActions: this.renderNavbarActions
            }
        },

        pageProps() {
            return {
                renderFooter: () => <Footer/>
            }
        }
    },

    methods: {
        toUserCenter() {
            const path = '/user'
            this.$route.path !== path && this.$router.push(path)
        },
        startGuide() {
            this.$guide(this.guideSteps)
        },
        logout() {
            if (this.$store.state.user.prepareLogout) {
                return
            }

            elConfirm('确认退出?').then(() => this.$store.dispatch('user/logout'))
        },

        openSettingDrawer() {
            this.$_settingDrawerInstance.visible = true
        },

        renderNavbarActions(defaultActions) {
            const customActions = [
                <notify-bell class="navbar-item"/>,
                <div
                    class="setting-btn navbar-item"
                    title="个性设置"
                    on-click={this.openSettingDrawer}
                >
                    <i class="el-icon-s-operation navbar-icon"/>
                </div>
            ]

            return customActions.concat(defaultActions.map(f => f()))
        }
    },

    //提前创建设置抽屉，避免初始化同步设置数据时导致layout重新渲染
    beforeCreate() {
        const Drawer = Vue.extend(SettingDrawer)
        const instance = new Drawer({data: {getRoot: () => this}}).$mount()
        document.body.appendChild(instance.$el)
        this.$_settingDrawerInstance = instance
    },

    //销毁时清除设置抽屉
    beforeDestroy() {
        if (this.$_settingDrawerInstance) {
            this.$_settingDrawerInstance.$destroy()
            this.$_settingDrawerInstance.$el.remove()
            delete this.$_settingDrawerInstance
        }
    },
}
</script>
