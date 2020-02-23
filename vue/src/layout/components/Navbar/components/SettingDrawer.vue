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
                <el-switch v-model="sidebarShowParent" :disabled="!sidebarCollapse" class="drawer-switch"/>
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
    export default {
        name: "SettingDrawer",
        props: {
            value: Boolean
        },
        computed: {
            showLogo: {
                get() {
                    return this.$store.state.setting.showLogo
                },
                set(v) {
                    this.$store.commit('setting/showLogo', v)
                }
            },
            showBreadcrumb: {
                get() {
                    return this.$store.state.setting.showBreadcrumb
                },
                set(v) {
                    this.$store.commit('setting/showBreadcrumb', v)
                }
            },
            sidebarUniqueOpen: {
                get() {
                    return this.$store.state.setting.sidebarUniqueOpen
                },
                set(v) {
                    this.$store.commit('setting/sidebarUniqueOpen', v)
                }
            },
            sidebarCollapse: {
                get() {
                    return this.$store.state.setting.sidebarCollapse
                },
                set(v) {
                    this.$store.commit('setting/sidebarCollapse', v)
                    if (v && this.sidebarAutoHidden) {
                        this.$store.commit('setting/sidebarAutoHidden', false)
                    }
                    if (!v && this.sidebarShowParent) {
                        this.$store.commit('setting/sidebarShowParent', false)
                    }
                }
            },
            sidebarShowParent: {
                get() {
                    return this.$store.state.setting.sidebarShowParent
                },
                set(v) {
                    this.$store.commit('setting/sidebarShowParent', v)
                }
            },
            sidebarAutoHidden: {
                get() {
                    return this.$store.state.setting.sidebarAutoHidden
                },
                set(v) {
                    this.$store.commit('setting/sidebarAutoHidden', v)
                    if (v && this.sidebarCollapse) {
                        this.$store.commit('setting/sidebarCollapse', false)
                    }
                }
            },
            headerAutoHidden: {
                get() {
                    return this.$store.state.setting.headerAutoHidden
                },
                set(v) {
                    this.$store.commit('setting/headerAutoHidden', v)
                }
            },
            showBackToTop: {
                get() {
                    return this.$store.state.setting.showBackToTop
                },
                set(v) {
                    this.$store.commit('setting/showBackToTop', v)
                }
            },
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
