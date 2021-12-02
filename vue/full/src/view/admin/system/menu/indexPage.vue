<template>
    <el-card>
        <div v-if="showTip" class="tip-row">线上版本没有开放动态路由功能</div>

        <el-row class="button-group">
            <template v-if="canAdd">
                <el-button icon="el-icon-plus" size="small" type="primary" @click="addRoot">添加根节点</el-button>
                <el-button icon="el-icon-plus" size="small" type="primary" @click="addChild">添加子节点</el-button>
            </template>

            <el-button v-if="canDel" icon="el-icon-delete" size="small" type="danger" @click="del">删 除</el-button>

            <el-dropdown placement="bottom" size="small" @command="more">
                <el-button plain size="small">
                    更多操作
                    <i class="el-icon-arrow-down el-icon--right"/>
                </el-button>

                <template v-slot:dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item :command="{action:'expand',level:0}">展开全部</el-dropdown-item>
                        <el-dropdown-item :command="{action:'collapse',level:0}">收起全部</el-dropdown-item>
                        <el-dropdown-item :command="{action:'expand',level:1}">仅展开一级</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </el-row>

        <el-row :gutter="100" style="margin-top: 20px;">
            <el-col :xs="24" :sm="11" :md="9" :lg="8" :xl="6">
                <el-alert show-icon :closable="false">
                    <template v-slot:title>
                        当前选择编辑：
                        <el-tag v-if="curNode" size="mini" closable @close="curNode = null">
                            {{ getNodeTitle(curNode) }}
                        </el-tag>
                    </template>
                </el-alert>

                <el-input
                    v-model="search"
                    placeholder="输入菜单名搜索"
                    suffix-icon="el-icon-search"
                    size="small"
                    style="margin-top: 10px"
                    @input="v => $refs.tree.filter(v)"
                />

                <div style="max-height: 660px;overflow: auto;margin-top: 10px">
                    <el-tree
                        ref="tree"
                        :data="menus"
                        node-key="id"
                        highlight-current
                        accordion
                        :expand-on-click-node="false"
                        :filter-node-method="filterNodeMethod"
                        @node-click="nodeClick"
                    >
                        <span slot-scope="{node}" class="el-tree-node__label">
                            <v-icon :icon="getNodeInfo(node).icon"/>
                            {{ getNodeTitle(node) }}
                        </span>
                    </el-tree>
                </div>
            </el-col>

            <el-col :xs="24" :sm="13" :md="15" :lg="13" :xl="11">
                <abstract-form v-if="showForm" :model="form" :rules="rules">
                    <el-form-item label="类型">
                        <el-select
                            v-if="showTypeSelector"
                            :value="form.type"
                            placeholder="请选择类型"
                            @change="typeChange"
                        >
                            <template v-slot:prefix>
                                <v-icon :icon="getNodeInfo({data:form}).icon"/>
                            </template>

                            <el-option
                                v-for="{label,value,icon} in nodeType"
                                :key="value"
                                :label="label"
                                :value="value"
                                :disabled="value === 3"
                            >
                                <v-icon :icon="icon"/>
                                {{ label }}
                            </el-option>
                        </el-select>

                        <template v-else>
                            <v-icon :icon="getNodeInfo({data:form}).icon"/>
                            {{ getNodeInfo({data: form}).label }}
                        </template>
                    </el-form-item>

                    <el-form-item v-if="form.pid" label="上级菜单">
                        <v-icon :icon="getParentNodeInfo().icon"/>
                        {{ getParentNodeInfo().title }}
                    </el-form-item>

                    <el-form-item label="名称" prop="title">
                        <el-input v-if="form.type === 3" v-model="form.name" maxlength="10"/>
                        <template v-else>{{ form.meta.title }}</template>
                    </el-form-item>

                    <el-form-item label="路径" prop="path">
                        <el-input v-model="form.path" maxlength="100"/>
                    </el-form-item>

                    <!--菜单独有配置项-->
                    <template v-if="form.type !== 3">
                        <!--叶子菜单独有配置项-->
                        <template v-if="form.type === 2">
                            <el-form-item label="路由名称" prop="name">
                                <el-input v-model="form.name" maxlength="50"/>
                            </el-form-item>
                            <el-form-item label="菜单组件" prop="component">
                                <el-input v-model="form.component" maxlength="50"/>
                            </el-form-item>
                        </template>

                        <el-form-item label="meta" prop="meta">
                            <json-editor v-model="form.metaStr" @input="metaChange"/>
                        </el-form-item>
                    </template>

                    <el-form-item label="admin独有">
                        <el-switch v-model="form.admin" active-text="是" inactive-text="否"/>
                    </el-form-item>

                    <el-form-item label="是否启用">
                        <el-switch v-model="form.enable" active-text="是" inactive-text="否"/>
                    </el-form-item>

                    <el-form-item v-if="showSaveBtn">
                        <el-button
                            :loading="loading"
                            icon="el-icon-edit-outline"
                            size="small"
                            type="primary"
                            @click="save"
                        >
                            保 存
                        </el-button>
                    </el-form-item>
                </abstract-form>
            </el-col>
        </el-row>
    </el-card>
</template>

<script>
import treePageMixin from '@/mixin/treePageMixin'
import AbstractForm from "@/component/abstract/Form"
import JsonEditor from "@/component/editor/JsonEditor"
import {add, update, del} from '@/api/system/resource'
import {isEmpty, mergeObj, resetObj} from "@/util"
import {wic} from "@/util/auth"
import {expandControl} from "@/util/element-ui/elTree"
import {elSuccess, elError, elConfirm} from "@/util/message"
import {nodeType, getNodeInfo, getNodeTitle} from "./common"

export default {
    name: "menuManagement",

    mixins: [treePageMixin],

    components: {AbstractForm, JsonEditor},

    data() {
        const validateTitle = (r, v, c) => {
            const value = getNodeTitle({data: this.form})
            c(isEmpty(value) ? '名称不能为空' : undefined)
        }
        return {
            showTip: process.env.NODE_ENV !== 'development',

            loading: false,
            search: null,
            type: 'add',// add or edit
            nodeType,
            form: {
                id: null,
                pid: null,
                type: null,
                name: null,
                path: null,
                component: null,
                meta: {
                    title: null,
                    dynamicTitle: null,
                    hidden: false,
                    sort: null,
                    icon: null,
                    affix: false,
                    noCache: false,
                    activeMenu: null,
                    noAuth: false,
                    iframe: null,
                    usePathKey: false,
                    useFullPathKey: false
                },
                metaStr: null,
                admin: false,
                enable: true
            },
            rules: {
                title: [{validator: validateTitle, trigger: 'change'}],
                path: [{required: true, message: '路径不能为空', trigger: 'change'}],
            }
        }
    },

    computed: {
        ...wic({add, update, del}),

        menus() {
            return this.$store.state.resource.resourceTree
        },
        showForm() {
            return this.type === 'add' && this.form.type === 0 || this.curNode
        },
        showTypeSelector() {
            //0.根节点的类型不能被改变
            const isRoot = this.form.type === 0
            //1.非数据接口
            const noApiNode = this.form.type !== 3
            //2.其下无子节点
            const noChild = this.type === 'add' && isRoot
                || this.type === 'edit' && this.curNode.childNodes.length <= 0
            //3.父节点只能为顶部或聚合菜单
            const parentIsFolderMenu =
                isRoot
                    ? true
                    : this.type === 'add' && [0, 1].includes(this.curNode.data.type)
                    || this.type === 'edit' && [0, 1].includes(this.curNode.parent.data.type)
            return !isRoot && noApiNode && noChild && parentIsFolderMenu
        },
        showSaveBtn() {
            return this.type === 'add' && this.canAdd || this.type === 'edit' && this.canUpdate
        }
    },

    methods: {
        getNodeInfo,
        getNodeTitle,
        getParentNodeInfo() {
            const node = this.type === 'add' ? this.curNode : this.curNode.parent
            const {icon} = getNodeInfo(node)
            return {icon, title: getNodeTitle(node)}
        },
        filterNodeMethod(value, data) {
            if (isEmpty(value)) return true
            return getNodeTitle({data}).includes(value)
        },
        afterNodeClick(data, node, ref, clear) {
            if (clear) return this.clearForm()
            this.type = 'edit'
            this.$refs.form && this.$refs.form.clearValidate()
            mergeObj(this.form, data)
            if (data.meta) {
                this.form.metaStr = JSON.stringify(data.meta, null, 2)
            }
        },
        //json编辑器值改变时
        metaChange(v) {
            try {
                v = JSON.parse(v)
            }
            catch (e) {
                return
            }
            resetObj(this.form.meta)
            mergeObj(this.form.meta, v)
        },
        //表单中菜单类型选择器改变时
        typeChange(v) {
            //从顶部或聚合改为页面菜单时，清空component
            if (v === 2 && [0, 1].includes(this.form.type)) {
                this.form.component = null
            }
            this.form.type = v
        },

        addRoot() {
            if (this.loading) return
            this.curNode = null
            this.type = 'add'
            this.clearForm()
            this.form.pid = 0
            this.form.type = 0
        },
        addChild() {
            if (this.loading) return
            if (!this.curNode) return elError('请选择一个节点')
            const {data: {id, type}} = this.curNode
            if (type === 3) return elError('数据接口不能作为父节点')
            this.type = 'add'
            this.clearForm()
            this.form.pid = id
            //如果父节点是叶子菜单，那么只能添加接口
            this.form.type = type === 2 ? 3 : 1
        },
        del() {
            if (this.loading) return
            if (!this.curNode) return elError('请选择要删除的节点')
            elConfirm(`确定删除【${getNodeTitle(this.curNode)}】?`)
                .then(() => {
                    this.loading = true
                    return del.request(this.curNode.data.id)
                })
                .then(() => {
                    elSuccess('删除成功')
                    this.init()
                })
                .finally(() => this.loading = false)
        },
        more({action, level}) {
            if (this.loading) return
            expandControl(this.$refs.tree, action, level)
        },

        init() {
            this.curNode = null
            this.clearForm()
            return this.$store.dispatch('resource/init')
        },
        clearForm() {
            resetObj(this.form)
            this.form.id = null
            this.form.pid = null
            this.form.type = null
            this.form.metaStr = JSON.stringify({title: ''}, null, 2)
            this.form.meta.sort = null
            this.form.enable = true
        },
        save() {
            if (this.loading) return
            this.$refs.form.validate(v => {
                if (!v) return
                this.loading = true
                const data = this.transformForm()
                const promise = this.type === 'add' ? add.request(data) : update.request(data)
                promise
                    .then(({data, msg}) => {
                        elSuccess(msg)
                        return this.init()
                    })
                    .finally(() => this.loading = false)
            })
        },
        //将this.form转换为真正的提交数据（meta替换为metaStr）
        transformForm() {
            return {...this.form, meta: this.form.metaStr, metaStr: undefined}
        }
    }
}
</script>
