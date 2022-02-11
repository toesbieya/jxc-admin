<template>
    <el-card v-loading="loading" class="card-container max-view-height full">
        <div class="department-outer">
            <div class="view-box">
                <org-tree-view
                    :data="data"
                    :horizontal="horizontal"
                    :zoom="realZoom"
                    collapsable
                    @node-contextmenu="openMenu"
                    @wheel.native.prevent="wheel"
                />
            </div>

            <div class="tooltip-box">
                <el-radio-group v-model="horizontal" fill="#909399" size="small" style="margin-right: 20px">
                    <el-radio-button :label="true">横 向</el-radio-button>
                    <el-radio-button :label="false">垂 直</el-radio-button>
                </el-radio-group>

                <el-button
                    circle
                    icon="el-icon-refresh"
                    size="small"
                    style="margin-right: 20px"
                    title="刷新"
                    type="info"
                    @click="search"
                />

                <zoom-control v-model="zoom" :max="200" :min="20"/>
            </div>
        </div>

        <edit-dialog v-model="editDialog" :data="currentNode" :type="type" @success="search"/>
    </el-card>
</template>

<script>
/**
 * 详情见iview-admin
 * https://admin.iviewui.com/component/org_tree_page
 */

import { useContextMenu } from 'el-admin-layout'
import EditDialog from "./component/EditDialog"
import OrgTreeView from "./component/OrgTreeView"
import ZoomControl from './component/ZoomControl'
import {add, update, del, get} from "@/api/system/department"
import {wic} from "@/util/auth"
import {elConfirm, elError, elSuccess} from "@/util/message"
import {createTreeByWorker} from "@/util/tree"

export default {
    name: "departmentManagement",

    components: {EditDialog, OrgTreeView, ZoomControl},

    data() {
        return {
            loading: false,
            data: {},
            zoom: 100,
            horizontal: false,
            currentNode: null,
            type: 'add',
            editDialog: false
        }
    },

    computed: {
        ...wic({add, update, del}),

        contextMenuItems() {
            return [
                this.canAdd && {content: '添 加', click: this.add},
                {content: '查 看', click: this.see},
                this.canUpdate && {content: '编 辑', click: this.edit},
                this.canDel && {content: '删 除', click: this.del}
            ]
        },

        realZoom() {
            return this.zoom / 100
        }
    },

    methods: {
        //将部门树转换为页面需要的结构
        completeDepartment(arr) {
            arr.forEach(i => {
                i.label = i.name
                i.parentName = i.fullname
                i.children && this.completeDepartment(i.children)
            })
        },

        //右键菜单
        openMenu(e, obj) {
            e.preventDefault()
            this.currentNode = obj

            // 销毁之前的右键菜单实例
            this.$contextmenu && this.$contextmenu()

            this.$contextmenu = useContextMenu(this.contextMenuItems, {
                left: e.clientX + 15,
                top: e.clientY
            })
        },

        //滚轮缩放
        wheel(e) {
            const eventDelta = e.wheelDelta || -(e.detail || 0)
            const zoom = this.zoom + eventDelta / 12 * 2
            if (zoom >= 20 && zoom <= 200) {
                this.zoom = zoom
            }
        },

        add() {
            this.type = 'add'
            this.editDialog = true
        },

        see() {
            this.type = 'see'
            this.editDialog = true
        },

        edit() {
            this.type = 'edit'
            this.editDialog = true
        },

        del() {
            if (this.loading || !this.currentNode) return
            if (this.currentNode.id === 1) return elError('根节点不能删除')
            if (this.currentNode.children && this.currentNode.children.length > 0) {
                return elError('请先删除子部门')
            }
            elConfirm(`确定删除部门【${this.currentNode.name}】？`)
                .then(() => {
                    this.loading = true
                    return del.request(this.currentNode)
                })
                .then(({msg}) => {
                    elSuccess(msg)
                    this.search()
                })
                .finally(() => this.loading = false)
        },

        search() {
            this.currentNode = null
            this.loading = true
            get
                .request()
                .then(({data}) => createTreeByWorker(data))
                .then(data => {
                    this.completeDepartment(data)
                    this.data = data[0]
                })
                .finally(() => this.loading = false)
        }
    },

    mounted() {
        this.search()
    }
}
</script>

<style lang="scss">
.department-outer {
    width: 100%;
    height: 100%;
    overflow: hidden;

    .tooltip-box {
        position: absolute;
        right: 30px;
        bottom: 30px;
        z-index: 2;
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }

    .view-box {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }
}
</style>
