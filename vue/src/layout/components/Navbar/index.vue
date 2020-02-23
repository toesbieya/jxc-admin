<template>
    <nav class="navbar">
        <breadcrumb v-if="showBreadcrumb"/>

        <div class="right-menu">
            <search id="header-search" class="right-menu-item"/>

            <fullscreen v-model="fullscreen" class="fullscreen-btn right-menu-item" title="全屏开关"/>

            <div class="setting-btn right-menu-item" @click="settingDrawer=true">
                <i class="el-icon-s-operation navbar-icon" title="个性设置"/>
            </div>

            <el-dropdown
                    class="avatar-container right-menu-item"
                    trigger="click"
                    @visible-change="$emit('menu-show',$event)"
            >
                <div class="avatar-wrapper">
                    <el-avatar :size="30" :src="avatar" icon="el-icon-user-solid"/>
                    <span>{{name}}</span>
                </div>
                <el-dropdown-menu class="user-dropdown" slot="dropdown">
                    <router-link to="/user/index">
                        <el-dropdown-item icon="el-icon-user">个人中心</el-dropdown-item>
                    </router-link>
                    <el-dropdown-item icon="el-icon-guide" @click.native="$guide(0,steps)">新手指引</el-dropdown-item>
                    <router-link v-if="showSystemMonitor" to="/system/monitor">
                        <el-dropdown-item icon="el-icon-monitor">系统监控</el-dropdown-item>
                    </router-link>
                    <router-link v-if="showSystemResource" to="/system/resource">
                        <el-dropdown-item icon="el-icon-setting">接口设置</el-dropdown-item>
                    </router-link>
                    <el-dropdown-item
                            divided
                            icon="el-icon-switch-button"
                            @click.native="logout"
                    >
                        退出登录
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
        <setting-drawer v-model="settingDrawer"/>
    </nav>
</template>

<script>
    import Breadcrumb from './components/Breadcrumb'
    import Search from './components/HeaderSearch'
    import Fullscreen from "./components/Fullscreen"
    import SettingDrawer from './components/SettingDrawer'
    import {mapState} from 'vuex'
    import GuideMixin from '@/mixins/guide'
    import {auth} from "@/utils/auth"
    import {elAlert, elConfirm} from "@/utils/message"

    export default {
        name: 'navbar',
        mixins: [GuideMixin.navbar],
        components: {Breadcrumb, Search, Fullscreen, SettingDrawer},
        data() {
            return {
                fullscreen: false,
                settingDrawer: false
            }
        },
        computed: {
            ...mapState('user', {
                avatar: state => state.avatar,
                name: state => state.name,
                prepare_logout: state => state.prepare_logout
            }),
            ...mapState('setting', {
                showBreadcrumb: state => state.showBreadcrumb
            }),
            showSystemMonitor() {
                return auth('/system/monitor')
            },
            showSystemResource() {
                return auth('/system/resource')
            }
        },
        methods: {
            logout() {
                if (this.prepare_logout) return
                elConfirm('确认退出?')
                    .then(() => this.$store.dispatch('user/logout'))
            }
        }
    }
</script>

<style lang="scss">
    .navbar {
        height: 50px;
        overflow: hidden;
        position: relative;
        background: #fff;
        box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

        .navbar-icon {
            font-size: 18px;
            cursor: pointer;
            vertical-align: middle;
        }

        .right-menu {
            float: right;
            height: 100%;
            line-height: 50px;

            &:focus {
                outline: none;
            }

            .right-menu-item {
                display: inline-block;
                padding: 0 8px;
                height: 100%;
                font-size: 18px;
                color: #5a5e66;
                vertical-align: text-bottom;
                transition: background .3s;
                cursor: pointer;

                &:hover:not(#header-search) {
                    background: rgba(0, 0, 0, .025)
                }
            }

            .avatar-container {
                margin-right: 30px;

                .avatar-wrapper {
                    margin-top: 5px;
                    position: relative;

                    .el-avatar {
                        position: relative;
                        top: 5px;
                        margin-right: 5px;
                    }
                }
            }
        }
    }
</style>
