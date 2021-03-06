<template>
    <div style="height: 100%">
        <el-admin-layout>
            <!--设置icon渲染-->
            <template v-slot:aside-menu-icon="{menu}">
                <v-icon :icon="menu.meta.icon" class="menu-icon"/>
            </template>
            <template v-slot:header-menu-icon="{menu}">
                <v-icon :icon="menu.meta.icon" class="menu-icon"/>
            </template>

            <!--自定义顶栏右侧元素-->
            <template v-slot:header-right="defaultContent">
                <header-right :default="defaultContent"/>
            </template>

            <!--页脚-->
            <template v-slot:page-footer>
                <v-footer/>
            </template>
        </el-admin-layout>

        <setting-drawer ref="setting-drawer"/>
    </div>
</template>

<script type="text/jsx">
import config from "@/config"
import ElAdminLayout, {appMutations, headerMutations} from 'el-admin-layout'
import HeaderRight from './component/HeaderRight'
import VFooter from './component/Footer'
import SettingDrawer from './component/SettingDrawer'
import offlineMixin from './mixin/offline'
import {elConfirm} from "@/util/message"

//设置一些基础信息
appMutations.title(config.title)
appMutations.logo(config.logo)

export default {
    name: "Layout",

    mixins: [offlineMixin],

    components: {ElAdminLayout, HeaderRight, VFooter, SettingDrawer},

    data() {
        return {
            guideSteps: [
                {
                    element: '.setting-btn.header-item',
                    content: '这是个性设置按钮，可以根据自己喜好进行一些设置',
                },
                {
                    element: '.header .el-dropdown.header-item',
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

    methods: {
        toUserCenter() {
            this.$router.push('/user', () => undefined)
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
            this.$refs['setting-drawer'].visible = true
        }
    },

    created() {
        //设置顶栏的用户头像和用户名称
        this.$watch(
            () => {
                const userInfo = this.$store.state.user
                return {
                    avatar: userInfo.avatar,
                    username: userInfo.name
                }
            },
            ({avatar, username}) => {
                headerMutations.avatar(avatar)
                headerMutations.username(username)
            },
            {immediate: true}
        )
        //设置顶栏的下拉菜单项
        headerMutations.dropdownItems([
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
        ])
    }
}
</script>
