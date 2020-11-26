<template>
    <el-drawer
        :visible="value"
        :with-header="false"
        append-to-body
        size="288px"
        @close="$emit('input', false)"
    >
        <div class="drawer-container">
            <el-divider>主题色</el-divider>
            <div class="drawer-item">
                <checkbox-group :value="appGetters.color" @input="changeThemeColor">
                    <color-checkbox v-for="i in colors" :key="i" :value="i"/>
                </checkbox-group>
            </div>

            <el-divider>导航模式</el-divider>
            <div class="drawer-item">
                <checkbox-group :value="appGetters.navMode" @input="changeNavMode">
                    <img-checkbox
                        v-for="{title, value, img} in navModes"
                        :key="value"
                        :title="title"
                        :value="value"
                        :img="img"
                    />
                </checkbox-group>
            </div>

            <el-divider>页面设置</el-divider>
            <div class="drawer-item">
                <span>显示logo</span>
                <el-switch :value="pageGetters.showLogo" @input="changePageShowLogo"/>
            </div>
            <div class="drawer-item">
                <span>分层结构</span>
                <el-select
                    :value="pageGetters.position"
                    size="mini"
                    style="width: 80px"
                    @input="changePagePosition"
                >
                    <el-option value="top-bottom" label="上下"/>
                    <el-option value="left-right" label="左右"/>
                </el-select>
            </div>
            <div class="drawer-item">
                <span>显示页头</span>
                <el-switch :value="pageGetters.showPageHeader" @input="changePageShowHeader"/>
            </div>
            <div class="drawer-item">
                <span>显示返回顶部按钮</span>
                <el-switch :value="pageGetters.showBackToTop" @input="changePageBackToTop"/>
            </div>

            <el-divider>侧边栏设置</el-divider>
            <div class="drawer-item">
                <span>主题风格</span>
                <el-select
                    :value="asideGetters.theme"
                    size="mini"
                    style="width: 80px"
                    @input="changeAsideTheme"
                >
                    <el-option value="light" label="亮色"/>
                    <el-option value="dark" label="暗色"/>
                </el-select>
            </div>
            <div class="drawer-item">
                <span>手风琴效果</span>
                <el-switch :value="asideGetters.uniqueOpen" @input="changeAsideUniqueOpen"/>
            </div>
            <div class="drawer-item">
                <span>折叠</span>
                <el-switch :value="asideGetters.collapse" @input="changeAsideCollapse"/>
            </div>
            <div class="drawer-item">
                <span>折叠时显示上级</span>
                <el-switch :value="asideGetters.showParentOnCollapse" @input="changeAsideCollapseParent"/>
            </div>
            <div class="drawer-item">
                <span>自动隐藏</span>
                <el-switch :value="asideGetters.autoHide" @input="changeAsideAutoHide"/>
            </div>
            <div class="drawer-item">
                <span>汉堡包位置</span>
                <el-select
                    :value="asideGetters.hamburgerPosition"
                    size="mini"
                    style="width: 90px"
                    @input="changeAsideHamburgerPosition"
                >
                    <el-option value="aside" label="侧边栏"/>
                    <el-option value="head" label="导航栏"/>
                </el-select>
            </div>

            <el-divider>导航栏设置</el-divider>
            <div class="drawer-item">
                <span>主题风格</span>
                <el-select
                    :value="navbarGetters.theme"
                    size="mini"
                    style="width: 80px"
                    @input="changeNavbarTheme"
                >
                    <el-option value="light" label="亮色"/>
                    <el-option value="dark" label="暗色"/>
                </el-select>
            </div>

            <el-divider>多页签设置</el-divider>
            <div class="drawer-item">
                <span>启用</span>
                <el-switch :value="tagsViewGetters.enabled" @input="changeTagsViewEnabled"/>
            </div>
            <div class="drawer-item">
                <span>启用快捷键切换</span>
                <el-switch :value="tagsViewGetters.shortcut" @input="changeTagsViewShortcut"/>
            </div>
            <div class="drawer-item">
                <span>持久化</span>
                <el-switch :value="tagsViewGetters.persistent" @input="changeTagsViewPersistent"/>
            </div>
        </div>
    </el-drawer>
</template>

<script>
import client from 'webpack-theme-color-replacer/client'
import forElementUI from 'webpack-theme-color-replacer/forElementUI'
import CheckboxGroup from "@/component/checkbox/Group"
import ColorCheckbox from "@/component/checkbox/ColorCheckbox"
import ImgCheckbox from "@/component/checkbox/ImgCheckbox"
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
import {getters as asideGetters, mutations as asideMutations} from "@/layout/store/aside"
import {getters as navbarGetters, mutations as navbarMutations} from "@/layout/store/navbar"
import {getters as pageGetters, mutations as pageMutations} from "@/layout/store/page"
import {getters as tagsViewGetters, mutations as tagsViewMutations} from "@/layout/store/tagsView"
import {mergeObj} from "@/util"
import {getLocalPersonalSettings, setLocalPersonalSettings} from "@/util/storage"

export default {
    name: "SettingDrawer",

    components: {CheckboxGroup, ColorCheckbox, ImgCheckbox},

    props: {value: Boolean},

    data() {
        return {
            appGetters, asideGetters, navbarGetters, pageGetters, tagsViewGetters,

            colors: ['#f5222d', '#fa541c', '#fadb14', '#3eaf7c', '#13c2c2', '#1890ff', '#722ed1', '#eb2f96'],
            navModes: [
                {
                    title: '侧边栏导航',
                    value: 'aside',
                    img: 'https://gw.alipayobjects.com/zos/antfincdn/XwFOFbLkSM/LCkqqYNmvBEbokSDscrm.svg'
                },
                {
                    title: '顶部导航',
                    value: 'head',
                    img: 'https://gw.alipayobjects.com/zos/antfincdn/URETY8%24STp/KDNDBbriJhLwuqMoxcAr.svg'
                },
                {
                    title: '混合导航',
                    value: 'mix',
                    img: 'https://gw.alipayobjects.com/zos/antfincdn/x8Ob%26B8cy8/LCkqqYNmvBEbokSDscrm.svg'
                },
                {
                    title: '双层侧边栏导航',
                    value: 'aside-two-part',
                    img: 'https://gw.alipayobjects.com/zos/antfincdn/XwFOFbLkSM/LCkqqYNmvBEbokSDscrm.svg'
                }
            ],

            setting: {
                app: {
                    color: '#1890ff',
                    navMode: 'mix'
                },
                page: {
                    showLogo: true,
                    position: 'left-right',
                    showPageHeader: true,
                    showBackToTop: true
                },
                aside: {
                    theme: 'light',
                    uniqueOpen: true,
                    collapse: false,
                    showParentOnCollapse: false,
                    autoHide: false,
                    hamburgerPosition: 'aside'
                },
                navbar: {
                    theme: 'light'
                },
                tagsView: {
                    enabled: true,
                    shortcut: true,
                    persistent: true
                }
            }
        }
    },

    methods: {
        //将此处的设置项数据同步到layout中的store
        syncLayoutStore() {
            const {app, page, aside, navbar, tagsView} = this.setting

            Object.entries(app).forEach(([k, v]) => appMutations[k](v))
            Object.entries(page).forEach(([k, v]) => pageMutations[k](v))
            Object.entries(aside).forEach(([k, v]) => asideMutations[k](v))
            Object.entries(navbar).forEach(([k, v]) => navbarMutations[k](v))
            Object.entries(tagsView).forEach(([k, v]) => tagsViewMutations[k](v))
        },
        //将layout中的store数据同步到此处的设置项
        syncSettingDrawer() {
            const {app, page, aside, navbar, tagsView} = this.setting

            Object.keys(app).forEach(k => app[k] = appGetters[k])
            Object.keys(page).forEach(k => page[k] = pageGetters[k])
            Object.keys(aside).forEach(k => aside[k] = asideGetters[k])
            Object.keys(navbar).forEach(k => navbar[k] = navbarGetters[k])
            Object.keys(tagsView).forEach(k => tagsView[k] = tagsViewGetters[k])
        },
        //将此处的设置项保存到本地
        saveSetting() {
            this.syncSettingDrawer()
            setLocalPersonalSettings(this.setting)
        },

        changeThemeColor(v) {
            client.changer.changeColor({newColors: forElementUI.getElementUISeries(v)})
            appMutations.color(v)
        },
        changeNavMode(v) {
            appMutations.navMode(v)
        },

        changePageShowLogo(v) {
            pageMutations.showLogo(v)
        },
        changePagePosition(v) {
            pageMutations.position(v)
        },
        changePageShowHeader(v) {
            pageMutations.showPageHeader(v)
        },
        changePageBackToTop(v) {
            pageMutations.showBackToTop(v)
        },

        changeAsideTheme(v) {
            asideMutations.theme(v)
        },
        changeAsideUniqueOpen(v) {
            asideMutations.uniqueOpen(v)
        },
        changeAsideCollapse(v) {
            asideMutations.collapse(v)
        },
        changeAsideCollapseParent(v) {
            asideMutations.showParentOnCollapse(v)
        },
        changeAsideAutoHide(v) {
            asideMutations.autoHide(v)
        },
        changeAsideHamburgerPosition(v) {
            asideMutations.hamburgerPosition(v)
        },

        changeNavbarTheme(v) {
            navbarMutations.theme(v)
        },

        changeTagsViewEnabled(v) {
            tagsViewMutations.enabled(v)
        },
        changeTagsViewShortcut(v) {
            tagsViewMutations.shortcut(v)
        },
        changeTagsViewPersistent(v) {
            tagsViewMutations.persistent(v)
        }
    },

    created() {
        //增强所有以change开头的方法
        for (const [key, value] of Object.entries(this)) {
            if (typeof value !== 'function' || !key.startsWith('change')) {
                continue
            }

            this[key] = (...arg) => {
                value.apply(this, arg)
                this.saveSetting()
            }
        }

        mergeObj(this.setting, getLocalPersonalSettings())

        //由于数据结构可能发生变化，所以在合并后覆盖本地数据
        setLocalPersonalSettings(this.setting)

        this.syncLayoutStore()

        //初始化时尝试修改主题色
        this.changeThemeColor(this.setting.app.color)
    }
}
</script>

<style lang="scss" scoped>
.drawer-container {
    padding: 24px;
    font-size: 14px;
    line-height: 1.5;
    word-wrap: break-word;

    .drawer-item {
        display: flex;
        justify-content: space-between;
        color: #606266;
        font-size: 14px;
        padding: 12px 0;
    }
}
</style>
