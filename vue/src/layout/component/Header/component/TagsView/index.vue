<script type="text/jsx">
import shortcutMixin from '@/layout/mixin/tagsViewShortcut'
import decideRouterTransitionMixin from '@/layout/mixin/decideRouterTransition'
import persistenceMixin from '@/layout/mixin/tagsViewPersistent'
import {route as routeConfig} from '@/config'
import {getters as appGetters} from "@/layout/store/app"
import {getters as tagsViewGetters, mutations as tagsViewMutations} from "@/layout/store/tagsView"
import ContextMenu from "@/component/menu/ContextMenu"
import ScrollPanel from './ScrollPanel'

export default {
    mixins: [shortcutMixin, decideRouterTransitionMixin, persistenceMixin],

    components: {ContextMenu, ScrollPanel},

    data() {
        return {
            contextmenu: {
                show: false,
                top: 0,
                left: 0,
            },
            selectedTag: {}
        }
    },

    computed: {
        visitedViews: () => tagsViewGetters.visitedViews,
        menus: () => appGetters.menus
    },

    watch: {
        $route(to) {
            this.addTag(to)
            this.$nextTick(this.moveToCurrentTag)
        }
    },

    methods: {
        //判断页签是否激活，考虑redirect刷新的情况
        isActive({path}) {
            const {path: routePath} = this.$route
            return routePath === path || routePath === `/redirect${path}`
        },
        isAffix(tag) {
            return tag.meta && tag.meta.affix
        },

        initTags() {
            //获取所有需要固定显示的页签
            function getAffixTags(menus) {
                const tags = []
                menus.forEach(({name, fullPath, children, meta}) => {
                    if (meta && meta.title && meta.affix) {
                        tags.push({
                            //注意，此处的fullPath并不是$route.fullPath，而是路由树拼接后的全路径
                            fullPath,
                            path: fullPath,
                            name,
                            meta: {...meta}
                        })
                    }
                    if (children) {
                        const tempTags = getAffixTags(children)
                        tempTags.length && tags.push(...tempTags)
                    }
                })
                return tags
            }

            //添加所有固定显示的页签
            for (const tag of getAffixTags(this.menus)) {
                tagsViewMutations.addVisitedView(tag)
            }

            //将当前路由对象添加为页签
            this.addTag(this.$route)
        },
        //将具有meta.title的路由对象添加为tab页
        addTag(to) {
            to.meta.title && tagsViewMutations.addView(to)
        },

        //横向滚动条移动至当前tab
        moveToCurrentTag() {
            //获取所有页签的componentInstance
            const tagInstances = this.$refs.scrollPanel.$children
            const tag = tagInstances.find(i => this.isActive(i.to))
            tag && this.$refs.scrollPanel.moveToTarget(tag, tagInstances)
        },

        /*
        * 右键菜单选项
        * 刷新所选、关闭所选、关闭其他、关闭所有
        * */
        refreshSelectedTag() {
            if (!this.selectedTag) return
            tagsViewMutations.delCachedView(this.selectedTag)
            this.$nextTick(() => this.$router.replace({path: `/redirect${this.selectedTag.fullPath}`}))
        },
        closeSelectedTag(view) {
            if (this.isAffix(view)) return
            tagsViewMutations.delView(view)
            this.isActive(view) && this.gotoLastView()
        },
        closeOthersTags() {
            tagsViewMutations.delOthersViews(this.selectedTag)
            //当前选中的页签不是当前路由时，跳转到选中页签的地址
            if (this.selectedTag.path !== this.$route.path) {
                return this.$router.push(this.selectedTag)
            }
        },
        closeAllTags() {
            tagsViewMutations.delAllViews()
            this.gotoLastView()
        },

        gotoLastView() {
            if (this.visitedViews.length === 0) {
                return this.$router.push('/')
            }
            const latest = this.visitedViews[this.visitedViews.length - 1]
            //只有当页签路径与当前路由路径不同时才跳转，否则刷新
            if (this.$route.path !== latest.path) {
                this.$router.push(latest.path)
            }
            else this.$router.replace(`/redirect${this.$route.fullPath}`)
        },

        openMenu(tag, e) {
            e.preventDefault()

            const menuMinWidth = 105
            const offsetLeft = this.$el.getBoundingClientRect().left // container margin left
            const offsetWidth = this.$el.offsetWidth // container width
            const maxLeft = offsetWidth - menuMinWidth // left boundary
            const left = e.clientX - offsetLeft + 15 // 15: margin right

            this.contextmenu.left = left > maxLeft ? maxLeft : left
            this.contextmenu.top = e.clientY
            this.contextmenu.show = true

            this.selectedTag = tag
        },

        renderTags(h) {
            return this.visitedViews.map(tag => {
                const {path, query, fullPath, meta: {title}} = tag
                const active = this.isActive(tag), affix = this.isAffix(tag)
                const closeSelectedTag = (e, tag) => {
                    e.preventDefault()
                    this.closeSelectedTag(tag)
                }
                return (
                    <router-link
                        key={path}
                        class={{'tags-view-item': true, active}}
                        to={{path, query, fullPath}}
                        tag="a"
                        v-on:contextmenu_native={e => this.openMenu(tag, e)}
                        v-on:dblclick_native={e => closeSelectedTag(e, tag)}
                    >
                        {title}
                        {!affix && <i class="el-icon-close" on-click={e => closeSelectedTag(e, tag)}/>}
                    </router-link>
                )
            })
        },
        renderContextMenu(h) {
            const menu = this.contextmenu
            const items = [
                {content: '刷新', click: this.refreshSelectedTag},
                this.isAffix(this.selectedTag)
                    ? undefined
                    : {
                        content: '关闭',
                        click: () => this.closeSelectedTag(this.selectedTag)
                    },
                {content: '关闭其他', click: this.closeOthersTags},
                {content: '关闭全部', click: this.closeAllTags}
            ]

            return <context-menu v-model={menu.show} items={items} left={menu.left} top={menu.top}/>
        }
    },

    beforeDestroy() {
        //销毁前将路由动画改为fade
        tagsViewMutations.transitionName(routeConfig.animate.default)
    },

    mounted() {
        this.initTags()
    },

    render(h) {
        return (
            <nav class="tags-view-container">
                <scroll-panel ref="scrollPanel" class="tags-view-wrapper">
                    {this.renderTags(h)}
                </scroll-panel>
                {this.renderContextMenu(h)}
            </nav>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
