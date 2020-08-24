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
import shortcutMixin from './mixin/shortcut'
import decideRouterTransitionMixin from './mixin/decideRouterTransition'
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
        visitedViews() {
            return this.$store.state.tagsView.visitedViews
        },
        menus() {
            return this.$store.state.resource.menus
        }
    },

    watch: {
        $route(to, from) {
            this.decideRouteTransition && this.decideRouteTransition(to, from)
            this.addTags(to).then(() => this.moveToCurrentTag())
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
                this.$store.commit('tagsView/addVisitedView', tag)
            }
        },

        //将当前具有title的路由添加为tab页
        addTags(to = this.$route) {
            return to.meta.title ? this.$store.dispatch('tagsView/addView', to) : Promise.resolve()
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
            this.$store.commit('tagsView/delCachedView', view)
            this.$nextTick(() => this.$router.replace({path: `/redirect${view.fullPath}`}))
        },
        closeSelectedTag(view) {
            if (this.isAffix(view)) return
            this.$store
                .dispatch('tagsView/delView', view)
                .then(() => this.isActive(view) && this.gotoLastView())
        },
        closeOthersTags() {
            this.$store
                .dispatch('tagsView/delOthersViews', this.selectedTag)
                .then(() => {
                    if (this.selectedTag.path !== this.$route.path) {
                        return this.$router.push(this.selectedTag)
                    }
                })
        },
        closeAllTags() {
            this.$store
                .dispatch('tagsView/delAllViews')
                .then(() => this.gotoLastView())
        },

        gotoLastView() {
            if (this.visitedViews.length === 0) return this.$router.push('/')
            const latest = this.visitedViews[this.visitedViews.length - 1]
            if (this.$route.path !== latest.path) this.$router.push(latest.path)
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
        this.$store.commit('tagsView/transitionName', 'el-fade-in-linear')
    },

    mounted() {
        this.initTags()
        this.addTags()
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
