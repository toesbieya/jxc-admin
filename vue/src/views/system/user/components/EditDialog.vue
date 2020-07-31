<template>
    <form-dialog :loading="loading" :title="title" :value="value" @close="cancel" @open="open">
        <el-form
            ref="form"
            :model="form"
            :rules="rules"
            label-position="right"
            label-width="75px"
            status-icon
        >
            <el-form-item label="名 称：" prop="name">
                <el-input v-model="form.name" :readonly="!canEdit" maxlength="20"/>
            </el-form-item>
            <el-form-item label="角 色：" prop="role">
                <role-selector
                    v-if="canEdit"
                    v-model="form.role"
                    @get-name="e => form.roleName = e"
                />
                <template v-else>{{ form.roleName }}</template>
            </el-form-item>
            <el-form-item label="部 门：" prop="dept">
                <department-selector
                    v-if="canEdit"
                    v-model="form.dept"
                    @get-name="e => form.deptName = e"
                />
                <template v-else>{{ form.deptName }}</template>
            </el-form-item>
            <el-form-item label="状 态：" prop="status">
                <el-radio-group v-model="form.status" :disabled="!canEdit">
                    <el-radio :label="1">启用</el-radio>
                    <el-radio :label="0">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>

        <template v-slot:footer>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canEdit" size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </form-dialog>
</template>

<script>
import dialogMixin from "@/mixins/dialogMixin"
import DepartmentSelector from "./DepartmentSelector"
import FormDialog from '@/components/FormDialog'
import RoleSelector from './RoleSelector'
import {checkName} from "@/api/account"
import {addUser, updateUser} from "@/api/system/user"
import {isEmpty} from '@/utils'
import {elConfirm} from "@/utils/message"

export default {
    name: "EditDialog",

    mixins: [dialogMixin],

    components: {DepartmentSelector, FormDialog, RoleSelector},

    props: {
        value: {type: Boolean, default: false},
        type: {type: String, default: 'see'},
        data: {type: Object, default: () => ({})},
    },

    data() {
        const validateName = (r, v, c) => {
            checkName(this.form.name, this.form.id)
                .then(({msg}) => msg ? c(msg) : c())
                .catch(e => c(e))
        }
        return {
            loading: false,
            form: {
                id: null,
                name: null,
                role: null,
                roleName: null,
                dept: null,
                deptName: null,
                status: 1,
            },
            rules: {
                name: [
                    {required: true, message: '用户名称不能为空', trigger: 'change'},
                    {validator: validateName, trigger: 'change'}
                ],
                role: [{required: true, message: '用户角色不能为空', trigger: 'change'}],
                dept: [{required: true, message: '用户部门不能为空', trigger: 'change'}],
                status: [{required: true, message: '用户状态不能为空', trigger: 'change'}],
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
                    return `确认添加新的用户【${this.form.name}】?`
                case 'edit':
                    return `确认提交对【${this.data.name}】作出的修改？`
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
                if (this.type === 'edit') {
                    Object
                        .keys(this.data)
                        .forEach(key => key in this.form && (this.form[key] = this.data[key]))
                }
            })
        },

        clearForm() {
            this.form.id = null
            this.form.name = null
            this.form.role = null
            this.form.roleName = null
            this.form.dept = null
            this.form.deptName = null
            this.form.status = 1
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
                elConfirm(this.confirmMessage)
                    .then(() => {
                        this.loading = true
                        return this.type === 'add' ? addUser(this.form) : updateUser(this.form)
                    })
                    .then(({msg}) => this.$emit('success', msg))
                    .finally(() => this.loading = false)
            })
        }
    }
}
</script>
