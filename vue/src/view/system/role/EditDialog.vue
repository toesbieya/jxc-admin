<template>
    <form-dialog :loading="loading" :title="title" :value="value" width="40%" @close="cancel" @open="open">
        <abstract-form :model="form" :rules="rules" size="">
            <el-form-item label="名 称" prop="name">
                <el-input v-model="form.name" maxlength="20"/>
            </el-form-item>
            <el-form-item label="状 态" prop="status">
                <el-radio-group v-model="form.status">
                    <el-radio :label="1">启用</el-radio>
                    <el-radio :label="0">禁用</el-radio>
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
                    :data="resource"
                    :props="{label:'name'}"
                    node-key="id"
                    show-checkbox
                />
            </el-form-item>
        </abstract-form>

        <template v-slot:footer>
            <el-dropdown placement="top" style="float: left" @command="treeCommand">
                <el-button plain size="small">
                    权限树操作
                    <i class="el-icon-arrow-up el-icon--right"/>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="{action:'expand',level:0}">展开全部</el-dropdown-item>
                    <el-dropdown-item :command="{action:'collapse',level:0}">收起全部</el-dropdown-item>
                    <el-dropdown-item :command="{action:'expand',level:1}">仅展开一级</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </form-dialog>
</template>

<script>
import dialogMixin from "@/mixin/dialogMixin"
import AbstractForm from "@/component/AbstractForm"
import FormDialog from '@/component/FormDialog'
import {getDepartments} from "@/api/system/department"
import {addRole, updateRole} from "@/api/system/role"
import {isEmpty, mergeObj} from '@/util'
import {createTreeByWorker, elTreeControl} from '@/util/tree'

export default {
    name: "EditDialog",

    mixins: [dialogMixin],

    components: {AbstractForm, FormDialog},

    props: {
        value: {type: Boolean, default: false},
        type: {type: String, default: 'add'},
        data: {type: Object, default: () => ({})},
    },

    data() {
        return {
            loading: false,
            form: {
                id: null,
                name: null,
                status: 1,
                scope: 1,
                departmentId: [],
                resourceId: []
            },
            rules: {
                name: [{required: true, message: '角色名称不能为空', trigger: 'change'}],
                status: [{required: true, message: '角色状态不能为空', trigger: 'change'}],
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

        resource() {
            return this.$store.state.resource.tree
        }
    },

    methods: {
        treeCommand({action, level}) {
            elTreeControl(this.$refs.perm, action, level)
        },

        open() {
            if (this.type !== 'edit') return
            this.$nextTick(() => {
                mergeObj(this.form, this.data)
                this.$refs.perm.setCheckedKeys(this.form.resourceId)
            })
        },

        clearForm() {
            this.form.id = null
            this.form.name = null
            this.form.status = 1
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
                const obj = {
                    ...this.form,
                    departmentId: this.$refs.dept ? this.$refs.dept.getCheckedKeys().join(',') : [],
                    resourceId: this.$refs.perm.getCheckedKeys().join(',')
                }
                const promise = this.type === 'add' ? addRole(obj) : updateRole(obj)
                promise
                    .then(({msg}) => this.$emit('success', msg))
                    .finally(() => this.loading = false)
            })
        }
    },

    mounted() {
        getDepartments(false)
            .then(data => createTreeByWorker(data))
            .then(data => {
                this.departments = data
            })
    }
}
</script>
