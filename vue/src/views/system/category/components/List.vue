<template>
    <el-card v-loading="loading" class="card-container max-view-height" header="分类列表">
        <el-scrollbar>
            <category-tree ref="tree" @node-click="see" @node-contextmenu="openContextMenu"/>
        </el-scrollbar>

        <context-menu v-model="contextmenu.show" :left="contextmenu.left" :top="contextmenu.top">
            <context-menu-item v-show="canAdd" @click="add">添加分类</context-menu-item>
            <context-menu-item v-show="currentCategory" @click="edit">编辑分类</context-menu-item>
            <context-menu-item v-show="currentCategory" @click="del">删除分类</context-menu-item>
        </context-menu>
    </el-card>
</template>

<script>
    import {isEmpty, waitUntilSuccess} from '@/utils'
    import {elConfirm, elError, elSuccess} from "@/utils/message"
    import {delCategory, getAllCategories} from "@/api/system/category"
    import ContextMenu from "@/components/ContextMenu"
    import ContextMenuItem from "@/components/ContextMenu/ContextMenuItem"
    import CategoryTree from '@/bizComponents/CategoryTree'

    export default {
        components: {ContextMenuItem, ContextMenu, CategoryTree},
        props: ['form'],
        data() {
            return {
                loading: false,
                maxLevel: 4,
                currentCategory: null,
                contextmenu: {
                    show: false,
                    left: 0,
                    top: 0
                }
            }
        },
        computed: {
            canAdd() {
                return !this.currentCategory
                    || this.currentCategory.type === 0 && this.currentCategory.level < this.maxLevel
            }
        },
        methods: {
            get() {
                this.loading = true
                this.currentCategory = null
                this.contextmenu.show = false
                getAllCategories()
                    .then(data => this.$store.commit('dataCache/categories', data))
                    .finally(() => this.loading = false)
            },
            add() {
                this.$emit('add', this.currentCategory)
            },
            see(obj, node) {
                this.currentCategory = {...obj, level: node.level}
                this.$emit('see', this.currentCategory, node.parent ? node.parent.data : null)
            },
            edit() {
                this.$emit('edit', this.currentCategory, this.currentCategory.parent)
            },
            del() {
                if (isEmpty(this.currentCategory)) return elError('请选择要删除的分类')
                if (this.currentCategory.children.length > 0) {
                    return elError('请先删除该分类下的子节点')
                }
                if (this.loading) return
                elConfirm(`确认删除【${this.currentCategory.name}】分类？`)
                    .then(() => {
                        this.loading = true
                        return delCategory({id: this.currentCategory.id, name: this.currentCategory.name})
                    })
                    .then(() => this.commitSuccess('删除成功'))
            },
            commitSuccess(msg) {
                elSuccess(msg)
                this.get()
            },
            openContextMenu(e, obj, node) {
                e.preventDefault()
                this.currentCategory = obj ? {
                    ...obj,
                    level: node.level,
                    parent: node.parent ? node.parent.data : null
                } : null
                this.$refs.tree.$refs.tree.setCurrentKey(obj ? obj.id : null)
                const {left, top} = this.$el.getBoundingClientRect()
                this.contextmenu.left = e.clientX - left + 15
                this.contextmenu.top = e.clientY - top
                this.contextmenu.show = true
            }
        },
        mounted() {
            this.$el.querySelector(".el-card__body").addEventListener('contextmenu', this.openContextMenu)
            waitUntilSuccess(
                () => !isEmpty(this.form()),
                () => this.form().$on('commit-success', this.commitSuccess)
            )
        },
        beforeDestroy() {
            this.$el.querySelector(".el-card__body").removeEventListener('contextmenu', this.openContextMenu)
        }
    }
</script>
<style scoped>
    .card-container {
        min-width: 250px;
    }
</style>
