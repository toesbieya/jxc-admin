<template>
    <nav class="navbar">
        <div>
            <hamburger class="navbar-item" :active="!sidebarCollapse" @click="sidebarCtrl"/>

            <div class="navbar-item" @click="refresh">
                <i class="el-icon-refresh-right navbar-icon" title="刷新"/>
            </div>

            <breadcrumb v-if="showBreadcrumb"/>
        </div>

        <div>
            <bell class="navbar-item"/>

            <div class="setting-btn navbar-item hidden-xs" @click="settingDrawer=true">
                <i class="el-icon-s-operation navbar-icon" title="个性设置"/>
            </div>

            <el-dropdown
                class="navbar-item"
                trigger="click"
                @visible-change="$emit('menu-show',$event)"
            >
                <div class="avatar-wrapper">
                    <el-avatar :size="30" :src="avatar" icon="el-icon-user-solid"/>
                    <span class="hidden-xs">{{ name }}</span>
                </div>
                <el-dropdown-menu class="user-dropdown" slot="dropdown">
                    <router-link to="/user">
                        <el-dropdown-item icon="el-icon-user">个人中心</el-dropdown-item>
                    </router-link>
                    <el-dropdown-item class="hidden-xs" icon="el-icon-guide" @click.native="()=>$guide(0,guideSteps)">
                        新手指引
                    </el-dropdown-item>
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
import {mapState} from 'vuex'
import GuideMixin from '@/mixin/guide'
import Bell from "./component/Bell"
import Breadcrumb from './component/Breadcrumb'
import Hamburger from './component/Hamburger'
import SettingDrawer from './component/SettingDrawer'
import {auth} from "@/util/auth"
import {elConfirm} from "@/util/message"

export default {
    name: 'navbar',

    mixins: [GuideMixin.navbar],

    components: {Hamburger, Breadcrumb, Bell, SettingDrawer},

    data() {
        return {
            settingDrawer: false
        }
    },

    computed: {
        ...mapState('user', ['avatar', 'name', 'prepareLogout']),

        ...mapState('setting', ['sidebarCollapse', 'showBreadcrumb'])
    },

    methods: {
        sidebarCtrl() {
            this.$store.commit('setting/sidebarCollapse', !this.sidebarCollapse)
        },

        refresh() {
            this.$router.replace({path: `/redirect${this.$route.fullPath}`})
        },

        logout() {
            if (this.prepareLogout) return
            elConfirm('确认退出?')
                .then(() => this.$store.dispatch('user/logout'))
        }
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
