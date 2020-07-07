<template>
    <nav class="navbar">
        <hamburger :is-active="!sidebarCollapse" @click="clickHamburger"/>

        <breadcrumb v-if="showBreadcrumb"/>

        <div class="right-menu">
            <search id="header-search" class="right-menu-item hidden-xs"/>

            <bell class="right-menu-item"/>

            <div class="setting-btn right-menu-item hidden-xs" @click="settingDrawer=true">
                <i class="el-icon-s-operation navbar-icon" title="个性设置"/>
            </div>

            <el-dropdown
                    class="avatar-container right-menu-item"
                    trigger="click"
                    @visible-change="$emit('menu-show',$event)"
            >
                <div class="avatar-wrapper">
                    <el-avatar :size="30" :src="avatar" icon="el-icon-user-solid"/>
                    <span class="hidden-xs">{{name}}</span>
                </div>
                <el-dropdown-menu class="user-dropdown" slot="dropdown">
                    <router-link to="/user/index">
                        <el-dropdown-item icon="el-icon-user">个人中心</el-dropdown-item>
                    </router-link>
                    <el-dropdown-item class="hidden-xs" icon="el-icon-guide" @click.native="()=>$guide(0,guideSteps)">
                        新手指引
                    </el-dropdown-item>
                    <router-link v-if="showSystemMonitor" to="/system/monitor">
                        <el-dropdown-item icon="el-icon-monitor">系统监控</el-dropdown-item>
                    </router-link>
                    <router-link v-if="showSystemResource" to="/system/resource">
                        <el-dropdown-item icon="el-icon-setting">接口设置</el-dropdown-item>
                    </router-link>
                    <el-dropdown-item divided icon="el-icon-switch-button" @click.native="logout">
                        退出登录
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>

        <setting-drawer v-model="settingDrawer"/>
    </nav>
</template>

<script>
    import Hamburger from './components/Hamburger'
    import Breadcrumb from './components/Breadcrumb'
    import Search from './components/HeaderSearch'
    import Bell from "./components/Bell"
    import SettingDrawer from './components/SettingDrawer'
    import {mapState} from 'vuex'
    import GuideMixin from '@/mixins/guide'
    import {auth} from "@/utils/auth"
    import {elConfirm} from "@/utils/message"

    export default {
        name: 'navbar',

        mixins: [GuideMixin.navbar],

        components: {Hamburger, Breadcrumb, Search, Bell, SettingDrawer},

        data() {
            return {
                settingDrawer: false
            }
        },

        computed: {
            ...mapState('user', ['avatar', 'name', 'prepare_logout']),

            ...mapState('setting', ['sidebarCollapse', 'showBreadcrumb']),

            showSystemMonitor() {
                return auth('/system/monitor')
            },

            showSystemResource() {
                return auth('/system/resource')
            }
        },

        methods: {
            clickHamburger() {
                this.$store.commit('setting/sidebarCollapse', !this.sidebarCollapse)
            },

            logout() {
                if (this.prepare_logout) return
                elConfirm('确认退出?')
                    .then(() => this.$store.dispatch('user/logout'))
            }
        }
    }
</script>

<style lang="scss" src="./style.scss"></style>
