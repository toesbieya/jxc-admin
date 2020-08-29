<template>
    <nav class="navbar">
        <div style="width: 30%">
            <hamburger class="navbar-item" :render="renderHamburger" :active="!sidebarCollapse" @click="sidebarCtrl"/>

            <breadcrumb v-if="showBreadcrumb"/>

            <root-menu :active-menu="activeRootMenu" :menu="menus" always-show/>
        </div>

        <div>
            <bell class="navbar-item"/>

            <div class="navbar-item" title="刷新" @click="refresh">
                <i class="el-icon-refresh-right navbar-icon"/>
            </div>

            <div class="setting-btn navbar-item hidden-xs" title="个性设置" @click="settingDrawer = true">
                <i class="el-icon-s-operation navbar-icon"/>
            </div>

            <el-dropdown
                class="navbar-item"
                trigger="click"
                @command="command"
                @visible-change="e => $emit('menu-show',e)"
            >
                <div class="avatar-wrapper">
                    <el-avatar :size="30" :src="avatar" icon="el-icon-user-solid"/>
                    <span class="hidden-xs">{{ name }}</span>
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
        </div>

        <setting-drawer v-model="settingDrawer"/>
    </nav>
</template>

<script>
import {mapState} from 'vuex'
import GuideMixin from '@/mixin/guide'
import Bell from "./component/Bell"
import Breadcrumb from './component/Breadcrumb'
import Hamburger from './component/Hamburger'
import RootMenu from "./component/RootMenu"
import SettingDrawer from './component/SettingDrawer'
import {getters as mainGetters} from "@/layout/store/main"
import {getters as settingGetters, mutations as settingMutations} from "@/layout/store/setting"
import {elConfirm} from "@/util/message"

export default {
    name: 'navbar',

    mixins: [GuideMixin.navbar],

    components: {Hamburger, Breadcrumb, Bell, RootMenu, SettingDrawer},

    data: () => ({settingDrawer: false}),

    computed: {
        device: () => mainGetters.device,
        activeRootMenu: () => mainGetters.activeRootMenu,
        menus: () => mainGetters.menus,

        ...mapState('user', ['avatar', 'name', 'prepareLogout']),

        sidebarAutoHidden: () => settingGetters.sidebarAutoHidden,
        sidebarCollapse: () => settingGetters.sidebarCollapse,
        showBreadcrumb: () => settingGetters.showBreadcrumb,

        renderHamburger() {
            return !(this.device === 'pc' && this.sidebarAutoHidden)
        }
    },

    methods: {
        sidebarCtrl() {
            settingMutations.sidebarCollapse(!this.sidebarCollapse)
        },

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
        }
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
