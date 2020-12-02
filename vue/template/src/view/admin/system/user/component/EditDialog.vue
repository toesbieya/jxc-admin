<template>
    <abstract-dialog :loading="loading" :title="title" :value="value" @close="cancel" @open="open">
        <abstract-form :model="form" :rules="rules" label-width="90px">
            <el-form-item label="登录名" prop="loginName">
                <el-input v-model="form.loginName" :readonly="!!form.id || !canEdit" maxlength="20"/>
            </el-form-item>
            <el-form-item label="昵 称" prop="nickName">
                <el-input v-model="form.nickName" :readonly="!canEdit" maxlength="100"/>
            </el-form-item>
            <el-form-item label="角 色" prop="role">
                <role-selector
                    v-if="canEdit"
                    v-model="form.role"
                    @get-name="e => form.roleName = e"
                />
                <template v-else>{{ form.roleName }}</template>
            </el-form-item>
            <el-form-item label="部 门" prop="dept">
                <department-selector
                    v-if="canEdit"
                    v-model="form.dept"
                    @get-name="e => form.deptName = e"
                />
                <template v-else>{{ form.deptName }}</template>
            </el-form-item>
            <el-form-item label="状 态" prop="enable">
                <el-radio-group v-model="form.enable" :disabled="!canEdit">
                    <el-radio :label="true">启用</el-radio>
                    <el-radio :label="false">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
        </abstract-form>

        <template v-slot:footer>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canEdit" size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </abstract-dialog>
</template>

<script>
import dialogMixin from "@/mixin/dialogMixin"
import AbstractForm from "@/component/abstract/Form"
import DepartmentSelector from "./DepartmentSelector"
import AbstractDialog from '@/component/abstract/Dialog'
import RoleSelector from './RoleSelector'
import {checkLoginName, checkNickName} from "@/api/account"
import {add, update} from "@/api/system/user"
import {isEmpty, debounce, mergeObj} from '@/util'

export default {
    name: "EditDialog",

    mixins: [dialogMixin],

    components: {AbstractForm, DepartmentSelector, AbstractDialog, RoleSelector},

    props: {
        value: Boolean,
        type: {type: String, default: 'see'},
        data: {type: Object, default: () => ({})},
    },

    data() {
        const validateLoginName = debounce((r, v, c) => {
            checkLoginName
                .request(this.form.loginName, this.form.id)
                .then(({msg}) => msg ? c(msg) : c())
                .catch(e => c(e))
        }, 300)
        const validateNickName = debounce((r, v, c) => {
            checkNickName
                .request(this.form.nickName, this.form.id)
                .then(({msg}) => msg ? c(msg) : c())
                .catch(e => c(e))
        }, 300)
        return {
            loading: false,
            form: {
                id: null,
                loginName: null,
                nickName: null,
                role: null,
                roleName: null,
                dept: null,
                deptName: null,
                enable: false
            },
            rules: {
                loginName: [
                    {required: true, message: '登录名不能为空', trigger: 'change'},
                    {validator: validateLoginName, trigger: 'change'}
                ],
                nickName: [
                    {required: true, message: '昵称不能为空', trigger: 'change'},
                    {validator: validateNickName, trigger: 'change'}
                ],
                role: [{required: true, message: '用户角色不能为空', trigger: 'change'}],
                dept: [{required: true, message: '用户部门不能为空', trigger: 'change'}]
            }
        }
    },

    computed: {
        title() {
            if (isEmpty(this.type)) return ''
            switch (this.type) {
                case 'see':
                    return '用户信息'
                case 'add':
                    return '添加用户'
                case 'edit':
                    return '编辑用户'
            }
        },

        confirmMessage() {
            switch (this.type) {
                case 'add':
                    return `确认添加新的用户【${this.form.loginName}】?`
                case 'edit':
                    return `确认提交对【${this.data.loginName}】作出的修改？`
                default:
                    return ''
            }
        },

        canEdit() {
            return ['add', 'edit'].includes(this.type)
        }
    },

    methods: {
        open() {
            this.$nextTick(() => {
                if (this.type !== 'add') {
                    mergeObj(this.form,this.data)
                }
            })
        },

        clearForm() {
            this.form.id = null
            this.form.loginName = null
            this.form.nickName = null
            this.form.role = null
            this.form.roleName = null
            this.form.dept = null
            this.form.deptName = null
            this.form.enable = false
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
                const promise = this.type === 'add' ? add.request(this.form) : update.request(this.form)
                promise
                    .then(({msg}) => this.$emit('success', msg))
                    .finally(() => this.loading = false)
            })
        }
    }
}
</script>
