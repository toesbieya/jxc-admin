<template>
    <el-drawer :visible="value" :with-header="false" append-to-body size="300px" @close="$emit('input', false)">
        <div class="drawer-container">
            <el-divider><h3>个性化设置</h3></el-divider>
            <div class="drawer-item">
                <span>显示Logo</span>
                <el-switch v-model="showLogo" class="drawer-switch"/>
            </div>
            <div class="drawer-item">
                <span>显示面包屑</span>
                <el-switch v-model="showBreadcrumb" class="drawer-switch"/>
            </div>
            <div class="drawer-item">
                <span>左侧菜单手风琴效果</span>
                <el-switch v-model="sidebarUniqueOpen" class="drawer-switch"/>
            </div>
            <div class="drawer-item">
                <span>左侧菜单折叠</span>
                <el-switch v-model="sidebarCollapse" class="drawer-switch"/>
            </div>
            <div class="drawer-item">
                <span>折叠菜单显示上级</span>
                <el-switch v-model="sidebarShowParent" class="drawer-switch"/>
            </div>
            <div class="drawer-item">
                <span>左侧菜单自动隐藏</span>
                <el-switch v-model="sidebarAutoHidden" class="drawer-switch"/>
            </div>
            <div class="drawer-item">
                <span>头部自动隐藏</span>
                <el-switch v-model="headerAutoHidden" class="drawer-switch"/>
            </div>
            <div class="drawer-item">
                <span>显示返回顶部按钮</span>
                <el-switch v-model="showBackToTop" class="drawer-switch"/>
            </div>
        </div>
    </el-drawer>
</template>

<script>
    const states = ['showLogo', 'showBreadcrumb', 'sidebarUniqueOpen', 'sidebarCollapse',
        'sidebarAutoHidden', 'sidebarShowParent', 'headerAutoHidden', 'showBackToTop']

    function createStateComputed() {
        const obj = {}
        states.forEach(key => {
            obj[key] = {
                get() {
                    return this.$store.state.setting[key]
                },
                set(v) {
                    this.$store.commit(`setting/${key}`, v)
                }
            }
        })
        return obj
    }

    export default {
        name: "SettingDrawer",
        props: {
            value: Boolean
        },
        computed: createStateComputed()
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
