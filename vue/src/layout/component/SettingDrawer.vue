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
                <span>logo位置</span>
                <el-select
                    :value="pageGetters.logoPosition"
                    size="mini"
                    style="width: 80px"
                    @input="changePageLogoPosition"
                >
                    <el-option value="aside" label="侧栏"/>
                    <el-option value="head" label="顶栏"/>
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
            appGetters, asideGetters, pageGetters, tagsViewGetters,

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
                    showPageHeader: true,
                    showBackToTop: true
                },
                aside: {
                    showLogo: true,
                    uniqueOpen: true,
                    collapse: false,
                    showParentOnCollapse: false,
                    autoHide: false
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
        //按照此处的设置项修改layout中的store
        updateLayoutStore() {
            const {app, page, aside, tagsView} = this.setting

            appMutations.color(app.color)
            appMutations.navMode(app.navMode)

            pageMutations.showPageHeader(page.showPageHeader)
            pageMutations.showBackToTop(page.showBackToTop)

            asideMutations.showLogo(aside.showLogo)
            asideMutations.uniqueOpen(aside.uniqueOpen)
            asideMutations.collapse(aside.collapse)
            asideMutations.showParentOnCollapse(aside.showParentOnCollapse)
            asideMutations.autoHide(aside.autoHide)

            tagsViewMutations.enabled(tagsView.enabled)
            tagsViewMutations.shortcut(tagsView.shortcut)
            tagsViewMutations.persistent(tagsView.persistent)
        },
        //将layout中的store数据同步到此处的设置项
        syncLayoutStore() {
            const {app, page, aside, tagsView} = this.setting
            app.color = appGetters.color
            app.navMode = appGetters.navMode

            page.showPageHeader = pageGetters.showPageHeader
            page.showBackToTop = pageGetters.showBackToTop

            aside.showLogo = asideGetters.showLogo
            aside.uniqueOpen = asideGetters.uniqueOpen
            aside.collapse = asideGetters.collapse
            aside.showParentOnCollapse = asideGetters.showParentOnCollapse
            aside.autoHide = asideGetters.autoHide

            tagsView.enabled = tagsViewGetters.enabled
            tagsView.shortcut = tagsViewGetters.shortcut
            tagsView.persistent = tagsViewGetters.persistent
        },
        //将此处的设置项保存到本地
        saveSetting() {
            this.syncLayoutStore()
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
        changePageLogoPosition(v) {
            pageMutations.logoPosition(v)
        },
        changePageShowHeader(v) {
            pageMutations.showPageHeader(v)
        },
        changePageBackToTop(v) {
            pageMutations.showBackToTop(v)
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
        for (const key of Object.keys(this)) {
            if (!key.startsWith('change')) continue
            const originMethod = this[key]
            this[key] = (...arg) => {
                originMethod.apply(this, arg)
                this.saveSetting()
            }
        }

        mergeObj(this.setting, getLocalPersonalSettings())

        //由于数据结构可能发生变化，所以在合并后覆盖本地数据
        setLocalPersonalSettings(this.setting)

        this.updateLayoutStore()
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
        color: rgba(0, 0, 0, .65);
        font-size: 14px;
        padding: 12px 0;
    }
}
</style>
