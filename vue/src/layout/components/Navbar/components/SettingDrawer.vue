<template>
    <el-drawer :visible="value" :with-header="false" append-to-body size="300px" @close="$emit('input', false)">
        <div class="drawer-container">
            <el-divider><h3>个性化设置</h3></el-divider>
            <div v-for="{name,key} in settings" :key="key" class="drawer-item">
                <span>{{name}}</span>
                <el-switch :value="getValue(key)" @input="setValue($event,key)" class="drawer-switch"/>
            </div>
        </div>
    </el-drawer>
</template>

<script>
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

    export default {
        name: "SettingDrawer",

        props: {
            value: Boolean
        },

        data: () => ({settings}),

        methods: {
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
