<template>
    <el-drawer :visible="value" :with-header="false" append-to-body size="288px" @close="$emit('input', false)">
        <div class="drawer-container">
            <el-divider>主题色</el-divider>
            <div class="drawer-item">
                <color-checkbox-group :value="color" @input="changeColor">
                    <color-check-box v-for="i in colors" :key="i" :color="i"/>
                </color-checkbox-group>
            </div>

            <el-divider>其他设置</el-divider>
            <div v-for="{name,key} in settings" :key="key" class="drawer-item">
                <span>{{ name }}</span>
                <el-switch :value="getValue(key)" class="drawer-switch" @input="e => setValue(e,key)"/>
            </div>
        </div>
    </el-drawer>
</template>

<script>
import ColorCheckBox from "@/components/ColorCheckBox"
import ColorCheckboxGroup from "@/components/ColorCheckBox/group"
import {isDev} from '@/config'
import client from 'webpack-theme-color-replacer/client'
import forElementUI from 'webpack-theme-color-replacer/forElementUI'

const settings = [
    {name: '显示logo', key: 'showLogo'},
    {name: '显示面包屑', key: 'showBreadcrumb'},
    {name: '使用多页签', key: 'useTagsView'},
    {name: '侧边栏手风琴效果', key: 'sidebarUniqueOpen'},
    {name: '侧边栏折叠', key: 'sidebarCollapse'},
    {name: '折叠菜单显示上级', key: 'sidebarShowParent'},
    {name: '侧边栏自动隐藏', key: 'sidebarAutoHidden'},
    {name: '头部自动隐藏', key: 'headerAutoHidden'},
    {name: '显示返回顶部按钮', key: 'showBackToTop'}
]
const colors = ['#f5222d', '#fa541c', '#fadb14', '#3eaf7c', '#13c2c2', '#1890ff', '#722ed1', '#eb2f96']

export default {
    name: "SettingDrawer",

    components: {ColorCheckBox, ColorCheckboxGroup},

    props: {value: Boolean},

    data() {
        return {
            settings,
            colors,
            color: '#1890ff'
        }
    },

    methods: {
        changeColor(color) {
            this.color = color
            isDev && client.changer.changeColor({newColors: forElementUI.getElementUISeries(color)})
        },
        getValue(key) {
            return this.$store.state.setting[key]
        },
        setValue(value, key) {
            this.$store.commit(`setting/${key}`, value)
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
