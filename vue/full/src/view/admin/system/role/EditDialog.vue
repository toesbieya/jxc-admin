<template>
    <abstract-dialog :loading="loading" :title="title" :value="value" width="40%" @close="cancel" @open="open">
        <abstract-form :model="form" :rules="rules">
            <el-form-item label="名 称" prop="name">
                <el-input v-model="form.name" maxlength="20"/>
            </el-form-item>
            <el-form-item label="状 态" prop="enable">
                <el-radio-group v-model="form.enable">
                    <el-radio :label="true">启用</el-radio>
                    <el-radio :label="false">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="数据范围" prop="scope">
                <el-select v-model="form.scope">
                    <el-option :value="1" label="全部"/>
                    <el-option :value="2" label="本部门"/>
                    <el-option :value="3" label="指定部门"/>
                </el-select>
            </el-form-item>
            <el-form-item v-if="form.scope === 3" label="指定部门">
                <el-tree
                    ref="dept"
                    :data="departments"
                    :props="{label:'name'}"
                    :default-checked-keys="form.departmentId"
                    node-key="id"
                    show-checkbox
                />
            </el-form-item>
            <el-form-item label="权 限">
                <el-tree
                    ref="perm"
                    :data="resourceTree"
                    node-key="id"
                    show-checkbox
                    accordion
                >
                    <span slot-scope="{node}" class="el-tree-node__label">
                        <v-icon :icon="getNodeInfo(node).icon"/>
                        {{ getNodeTitle(node) }}
                    </span>
                </el-tree>
            </el-form-item>
        </abstract-form>

        <template v-slot:footer>
            <el-dropdown placement="top" style="float: left" @command="treeCommand">
                <el-button plain size="small">
                    权限树操作
                    <i class="el-icon-arrow-up el-icon--right"/>
                </el-button>

                <template v-slot:dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item :command="{action:'expand',level:0}">展开全部</el-dropdown-item>
                        <el-dropdown-item :command="{action:'collapse',level:0}">收起全部</el-dropdown-item>
                        <el-dropdown-item :command="{action:'expand',level:1}">仅展开一级</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>

            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canEdit" size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </abstract-dialog>
</template>

<script>
import dialogMixin from "@/mixin/dialogMixin"
import AbstractForm from "@/component/abstract/Form"
import AbstractDialog from '@/component/abstract/Dialog'
import {get as getDepartments} from "@/api/system/department"
import {add, update} from "@/api/system/role"
import {isEmpty, mergeObj} from '@/util'
import {expandControl} from "@/util/element-ui/elTree"
import {createTreeByWorker} from '@/util/tree'
import {getNodeTitle, getNodeInfo} from "@/view/admin/system/menu/common"

export default {
    name: "EditDialog",

    mixins: [dialogMixin],

    components: {AbstractForm, AbstractDialog},

    props: {
        value: Boolean,
        type: {type: String, default: 'add'},
        data: {type: Object, default: () => ({})},
    },

    data() {
        return {
            loading: false,
            form: {
                id: null,
                name: null,
                enable: false,
                scope: 1,
                departmentId: [],
                resourceId: []
            },
            rules: {
                name: [{required: true, message: '角色名称不能为空', trigger: 'change'}],
                scope: [{required: true, message: '数据范围不能为空', trigger: 'change'}],
            },
            departments: []
        }
    },

    computed: {
        title() {
            if (isEmpty(this.type)) return ''
            switch (this.type) {
                case 'add':
                    return '添加角色'
                case 'edit':
                    return '编辑角色'
            }
        },

        confirmMessage() {
            switch (this.type) {
                case 'add':
                    return `确认添加新的角色【${this.form.name}】?`
                case 'edit':
                    return `确认提交对【${this.data.name}】作出的修改？`
                default:
                    return ''
            }
        },

        resourceTree() {
            return this.$store.state.resource.resourceTree
        },

        canEdit() {
            return ['add', 'edit'].includes(this.type)
        }
    },

    methods: {
        getNodeInfo,
        getNodeTitle,
        treeCommand({action, level}) {
            expandControl(this.$refs.perm, action, level)
        },
        open() {
            if (this.type === 'add') return
            this.$nextTick(() => {
                mergeObj(this.form, this.data)
                this.$refs.perm.setCheckedKeys(this.form.resourceId)
            })
        },

        clearForm() {
            this.form.id = null
            this.form.name = null
            this.form.enable = false
            this.form.scope = 1
            this.form.departmentId = []
            this.form.resourceId = []
            this.$refs.perm.setCheckedKeys([])
            this.$refs.dept && this.$refs.dept.setCheckedKeys([])
            this.$nextTick(() => this.$refs.form.clearValidate())
        },
        cancel() {
            this.closeDialog()
            this.clearForm()
        },
        confirm() {
            if (this.loading) return
            this.$refs.form.validate(v => {
                if (!v) return
                this.loading = true
                const data = {
                    ...this.form,
                    departmentId: this.getCheckedDept(),
                    resourceId: this.getCheckedRes()
                }
                const promise = this.type === 'add' ? add.request(data) : update.request(data)
                promise
                    .then(({msg}) => this.$emit('success', msg))
                    .finally(() => this.loading = false)
            })
        },
        getCheckedDept() {
            const ref = this.$refs.dept
            return ref ? ref.getCheckedKeys().join(',') : null
        },
        getCheckedRes() {
            const ref = this.$refs.perm
            return ref ? ref.getCheckedKeys().join(',') : null
        }
    },

    mounted() {
        getDepartments
            .request(false)
            .then(({data}) => createTreeByWorker(data))
            .then(data => {
                this.departments = data
            })
    }
}
</script>
