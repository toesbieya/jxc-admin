<template>
    <el-card v-loading="loading" class="card-container max-view-height full" header="分类列表">
        <el-scrollbar>
            <category-tree ref="tree" @node-click="see" @node-contextmenu="openContextMenu"/>
        </el-scrollbar>
    </el-card>
</template>

<script>
import { useContextMenu } from 'el-admin-layout'
import CategoryTree from '@/component/biz/CategoryTree'
import {add, del, getAll} from "@/api/system/category"
import {isEmpty, waitUntilSuccess} from '@/util'
import {auth} from "@/util/auth"
import {elConfirm, elError, elSuccess} from "@/util/message"

export default {
    components: {CategoryTree},

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
            return auth(add.url) && !this.currentCategory
                || !this.currentCategory.leaf && this.currentCategory.level < this.maxLevel
        },

        contextMenuItems() {
            const items = []

            this.canAdd && items.push({content: '添加分类', click: this.add})
            this.currentCategory && items.push({content: '编辑分类', click: this.edit})
            this.currentCategory && items.push({content: '删除分类', click: this.del})

            return items
        }
    },

    methods: {
        get() {
            this.loading = true
            this.currentCategory = null
            getAll
                .request()
                .then(({data}) => this.$store.commit('dataCache/categories', data))
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
                    return del.request({id: this.currentCategory.id, name: this.currentCategory.name})
                })
                .then(() => this.commitSuccess('删除成功'))
        },

        commitSuccess(msg) {
            elSuccess(msg)
            this.get()
        },

        openContextMenu(e, obj, node) {
            e.preventDefault()

            // 销毁之前的右键菜单实例
            this.$contextmenu && this.$contextmenu()

            this.currentCategory = obj ? {
                ...obj,
                level: node.level,
                parent: node.parent ? node.parent.data : null
            } : null

            this.$refs.tree.$refs.tree.setCurrentKey(obj ? obj.id : null)

            this.$contextmenu = useContextMenu(this.contextMenuItems, {
                left: e.clientX + 15,
                top: e.clientY
            })
        }
    },

    mounted() {
        waitUntilSuccess(
            () => !isEmpty(this.form()),
            () => this.form().$on('commit-success', this.commitSuccess)
        )

        this.$el.querySelector(".el-card__body").addEventListener('contextmenu', this.openContextMenu)

        this.$once('hook:beforeDestroy', () => {
            this.$el.querySelector(".el-card__body").removeEventListener('contextmenu', this.openContextMenu)
        })
    }
}
</script>

<style scoped>
.card-container {
    min-width: 250px;
}
</style>
