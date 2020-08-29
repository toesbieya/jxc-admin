<template>
    <nav class="tags-view-container">
        <scroll-pane ref="scrollPane" class="tags-view-wrapper">
            <router-link
                ref="tag"
                v-for="tag in visitedViews"
                :key="tag.path"
                :class="{'tags-view-item': true, active: isActive(tag)}"
                :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
                tag="div"
                @contextmenu.prevent.stop.native="e => openMenu(tag, e)"
                @dblclick.prevent.stop.native="() => closeSelectedTag(tag)"
            >
                {{ tag.title }}
                <i v-if="!isAffix(tag)" class="el-icon-close" @click.prevent.stop="() => closeSelectedTag(tag)"/>
            </router-link>
        </scroll-pane>

        <context-menu v-model="contextmenu.show" :left="contextmenu.left" :top="contextmenu.top">
            <context-menu-item @click="() => refreshSelectedTag(selectedTag)">刷新</context-menu-item>
            <context-menu-item v-if="!isAffix(selectedTag)" @click="() => closeSelectedTag(selectedTag)">
                关闭
            </context-menu-item>
            <context-menu-item @click="closeOthersTags">关闭其他</context-menu-item>
            <context-menu-item @click="closeAllTags">关闭全部</context-menu-item>
        </context-menu>
    </nav>
</template>

<script type="text/jsx">
import shortcutMixin from '@/layout/mixin/shortcut'
import decideRouterTransitionMixin from '@/layout/mixin/decideRouterTransition'
import {route as routeConfig} from '@/config'
import {getters as mainGetters} from "@/layout/store/main"
import {getters as tagsViewGetters, mutations as tagsViewMutations} from "@/layout/store/tagsView"
import ContextMenu from "@/component/ContextMenu"
import ContextMenuItem from "@/component/ContextMenu/item"
import ScrollPane from './ScrollPane'

export default {
    mixins: [shortcutMixin, decideRouterTransitionMixin],

    components: {ContextMenu, ContextMenuItem, ScrollPane},

    data() {
        return {
            contextmenu: {
                show: false,
                top: 0,
                left: 0,
            },
            selectedTag: {},
            affixTags: [],
            scroller: null
        }
    },

    computed: {
        visitedViews: () => tagsViewGetters.visitedViews,
        menus: () => mainGetters.menus
    },

    watch: {
        $route(to, from) {
            this.decideRouteTransition && this.decideRouteTransition(to, from)
            this.addTags(to)
            this.moveToCurrentTag()
        },
        'contextmenu.show'(v) {
            this.$emit('menu-show', v)
        }
    },

    methods: {
        isActive(route) {
            return route.path === this.$route.path
        },
        isAffix(tag) {
            return tag.meta && tag.meta.affix
        },

        filterAffixTags(menus) {
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
                    const tempTags = this.filterAffixTags(children)
                    tempTags.length && tags.push(...tempTags)
                }
            })
            return tags
        },
        initTags() {
            this.affixTags = this.filterAffixTags(this.menus)
            for (const tag of this.affixTags) {
                tagsViewMutations.addVisitedView(tag)
            }
        },

        //将当前具有meta.title的路由添加为tab页
        addTags(to = this.$route) {
            to.meta.title && tagsViewMutations.addView(to)
        },

        //横向滚动条移动至当前tab
        moveToCurrentTag() {
            this.$nextTick(() => {
                const tag = this.$refs.tag.find(i => i.to.path === this.$route.path)
                tag && this.$refs.scrollPane.moveToTarget(tag)
            })
        },

        /*
        * 右键菜单选项
        * 刷新所选、关闭所选、关闭其他、关闭所有
        * */
        refreshSelectedTag(view) {
            tagsViewMutations.delCachedView(view)
            this.$nextTick(() => this.$router.replace({path: `/redirect${view.fullPath}`}))
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
            const menuMinWidth = 105
            const offsetLeft = this.$el.getBoundingClientRect().left // container margin left
            const offsetWidth = this.$el.offsetWidth // container width
            const maxLeft = offsetWidth - menuMinWidth // left boundary
            const left = e.clientX - offsetLeft + 15 // 15: margin right

            this.contextmenu.left = left > maxLeft ? maxLeft : left
            this.contextmenu.top = e.clientY
            this.contextmenu.show = true

            this.selectedTag = tag
        }
    },

    beforeDestroy() {
        //销毁前将路由动画改为fade
        tagsViewMutations.transitionName(routeConfig.animate.default)
    },

    mounted() {
        this.initTags()
        this.addTags()
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
