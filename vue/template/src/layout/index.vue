<template>
    <el-admin-layout :navbar-props="navbarProps" :page-props="pageProps"/>
</template>

<script type="text/jsx">
import Vue from 'vue'
import config from "@/config"
import ElAdminLayout, {setIconRenderer, appMutations} from 'el-admin-layout'
import Footer from './component/Footer'
import SettingDrawer from './component/SettingDrawer'
import tagsViewPersistent from './mixin/tagsViewPersistent'
import tagsViewShortcut from './mixin/tagsViewShortcut'
import {elConfirm} from "@/util/message"

//设置一些基础信息
appMutations.title(config.title)
appMutations.logo(config.logo)

//设置图标渲染方式
setIconRenderer((h, data) => <v-icon {...data}/>)

export default {
    name: "Layout",

    mixins: [tagsViewPersistent, tagsViewShortcut],

    components: {ElAdminLayout},

    computed: {
        navbarProps() {
            const userInfo = this.$store.state.user
            return {
                user: {
                    avatar: userInfo.avatar,
                    name: userInfo.name
                },
                userDropdownItems: [
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
        logout() {
            if (this.$store.state.user.prepareLogout) {
                return
            }

            elConfirm('确认退出?').then(() => this.$store.dispatch('user/logout'))
        },

        openSettingDrawer() {
            appMutations.showSettingDrawer(true)
        },

        renderNavbarActions(defaultActions) {
            const customActions = [
                <div
                    class="setting-btn navbar-item"
                    title="个性设置"
                    on-click={this.openSettingDrawer}
                >
                    <i class="el-icon-s-operation navbar-icon"/>
                </div>
            ]

            return customActions.concat(defaultActions)
        }
    },

    //提前创建设置抽屉，避免初始化同步设置数据时导致layout重新渲染
    beforeCreate() {
        const Drawer = Vue.extend(SettingDrawer)
        const instance = new Drawer().$mount()
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
