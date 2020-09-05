<template>
    <el-drawer :visible="value" :with-header="false" append-to-body size="288px" @close="$emit('input', false)">
        <div class="drawer-container">
            <el-divider>主题色</el-divider>
            <div class="drawer-item">
                <checkbox-group :value="color" @input="changeColor">
                    <color-checkbox v-for="i in colors" :key="i" :value="i"/>
                </checkbox-group>
            </div>

            <el-divider>导航模式</el-divider>
            <div class="drawer-item">
                <checkbox-group :value="navMode" @input="e => setValue(e, 'navMode')">
                    <img-checkbox
                        v-for="{title, value, img} in navModes"
                        :key="value"
                        :title="title"
                        :value="value"
                        :img="img"
                    />
                </checkbox-group>
            </div>

            <template v-for="{title, children} in settings">
                <el-divider>{{ title }}</el-divider>
                <div v-for="{title, key} in children" :key="key" class="drawer-item">
                    <span>{{ title }}</span>
                    <el-switch :value="getValue(key)" class="drawer-switch" @input="e => setValue(e, key)"/>
                </div>
            </template>
        </div>
    </el-drawer>
</template>

<script>
import client from 'webpack-theme-color-replacer/client'
import forElementUI from 'webpack-theme-color-replacer/forElementUI'
import {isDev} from '@/config'
import CheckboxGroup from "@/component/checkbox/Group"
import ColorCheckbox from "@/component/checkbox/ColorCheckbox"
import ImgCheckbox from "@/component/checkbox/ImgCheckbox"
import {getters, mutations} from "@/layout/store/setting"

export default {
    name: "SettingDrawer",

    components: {CheckboxGroup, ColorCheckbox, ImgCheckbox},

    props: {value: Boolean},

    data() {
        return {
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
            ],
            settings: [
                {
                    title: '页面设置',
                    children: [
                        {title: '显示logo', key: 'showLogo'},
                        {title: '显示页头', key: 'showPageHeader'},
                        {title: '使用多页签', key: 'useTagsView'}
                    ]
                },
                {
                    title: '侧边栏设置',
                    children: [
                        {title: '手风琴效果', key: 'sidebarUniqueOpen'},
                        {title: '折叠', key: 'sidebarCollapse'},
                        {title: '折叠状态下显示上级', key: 'sidebarShowParent'},
                        {title: '自动隐藏', key: 'sidebarAutoHidden'},
                    ]
                },
                {
                    title: '其他设置',
                    children: [
                        {title: '显示返回顶部按钮', key: 'showBackToTop'}
                    ]
                }
            ]
        }
    },

    computed: {
        color: () => getters.themeColor,
        navMode: () => getters.navMode,
    },

    methods: {
        changeColor(color) {
            this.setValue(color,'themeColor')
            isDev && client.changer.changeColor({newColors: forElementUI.getElementUISeries(color)})
        },

        getValue(key) {
            return getters[key]
        },
        setValue(value, key) {
            mutations[key](value)
        }
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
        color: rgba(0, 0, 0, .65);
        font-size: 14px;
        padding: 12px 0;
    }

    .drawer-switch {
        float: right
    }
}
</style>
