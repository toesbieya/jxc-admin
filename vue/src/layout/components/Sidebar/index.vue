<template>
    <aside
            :class="{'collapse-sidebar':sidebarCollapse,'hide-sidebar':hideSidebar}"
            class="sidebar-container"
            @mouseenter="mouseOutside=false"
            @mouseleave="mouseOutside=true"
    >
        <logo v-if="showLogo" :collapse="sidebarCollapse"/>
        <el-scrollbar>
            <el-menu
                    ref="menu"
                    :active-text-color="variables.primary"
                    :background-color="variables.menuBg"
                    :collapse="sidebarCollapse"
                    :collapse-transition="false"
                    :default-active="$route.path"
                    :text-color="variables.menuText"
                    :unique-opened="sidebarUniqueOpen"
                    mode="vertical"
                    @select="select"
            >
                <sidebar-item
                        v-for="route in routes"
                        :key="route.path"
                        :show-parent="sidebarShowParent"
                        :collapse="sidebarCollapse"
                        :item="route"
                />
            </el-menu>
        </el-scrollbar>
    </aside>
</template>

<script>
    import {mapState} from 'vuex'
    import Logo from './components/Logo'
    import SidebarItem from './components/SidebarItem'
    import variables from '@/assets/styles/variables.scss'

    export default {
        name: 'sidebar',
        components: {SidebarItem, Logo},
        data() {
            return {
                variables,
                mouseOutside: true
            }
        },
        computed: {
            ...mapState('resource', {
                routes: state => state.sidebarMenus
            }),
            ...mapState('setting', {
                showLogo: state => state.showLogo,
                sidebarUniqueOpen: state => state.sidebarUniqueOpen,
                sidebarCollapse: state => state.sidebarCollapse,
                sidebarShowParent: state => state.sidebarShowParent,
                sidebarAutoHidden: state => state.sidebarAutoHidden
            }),
            hideSidebar() {
                return this.sidebarAutoHidden && this.mouseOutside
            }
        },
        watch: {
            hideSidebar(v) {
                if (v) document.addEventListener('mousemove', this.moveEvent)
                else document.removeEventListener('mousemove', this.moveEvent)
            }
        },
        methods: {
            moveEvent(e) {
                if (e.clientX <= 15) this.mouseOutside = false
            },
            select(index) {
                if (this.$route.path === index) {
                    this.$store.commit('tagsView/DEL_CACHED_VIEW', this.$route)
                    this.$nextTick(() => this.$router.replace({path: '/redirect' + this.$route.fullPath}))
                }
                else this.$router.push(index)
            }
        },
        mounted() {
            if (this.sidebarAutoHidden) {
                document.addEventListener('mousemove', this.moveEvent)
            }
        },
        beforeDestroy() {
            document.removeEventListener('mousemove', this.moveEvent)
        }
    }
</script>
