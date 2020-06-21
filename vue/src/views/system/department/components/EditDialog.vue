<template>
    <dialog-form :loading="loading" :title="title" :value="value" @close="cancel" @open="open">
        <el-form
                ref="form"
                :model="form"
                :rules="rules"
                label-position="right"
                label-width="100px"
                status-icon
        >
            <el-form-item v-show="parentName" label="上级部门：">
                <el-input :value="parentName" readonly/>
            </el-form-item>
            <el-form-item label="部门名称：" prop="name">
                <el-input v-model="form.name" maxlength="20"/>
            </el-form-item>
            <el-form-item label="部门状态：" prop="status">
                <el-radio-group v-model="form.status">
                    <el-radio :label="1">启用</el-radio>
                    <el-radio :label="0">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>

        <template v-slot:footer>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </dialog-form>
</template>

<script>
    import DialogForm from '@/components/DialogForm'
    import dialogMixin from "@/mixins/dialogMixin"
    import {addDepartment, updateDepartment} from "@/api/system/department"
    import {isEmpty} from '@/utils'
    import {elConfirm, elSuccess} from "@/utils/message"

    export default {
        name: "EditDialog",

        mixins: [dialogMixin],

        components: {DialogForm},

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
                    pid: null,
                    name: null,
                    status: 1,
                },
                rules: {
                    name: [{required: true, message: '部门名称不能为空', trigger: 'change'}],
                    status: [{required: true, message: '部门状态不能为空', trigger: 'change'}]
                }
            }
        },

        computed: {
            title() {
                if (!this.data || isEmpty(this.type)) return ''
                switch (this.type) {
                    case 'add':
                        return '添加部门'
                    case 'edit':
                        return `编辑部门【${this.data.name}】`
                }
            },

            confirmMessage() {
                if (!this.data || isEmpty(this.type)) return ''
                switch (this.type) {
                    case 'add':
                        return `确认添加新的部门【${this.form.name}】?`
                    case 'edit':
                        return `确认提交对【${this.data.name}】作出的修改？`
                    default:
                        return ''
                }
            },

            parentName() {
                if (!this.data) return ''
                if (this.type === 'add') return this.data.fullname
                else return this.data.parentName
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
                    else this.form.pid = this.data.id
                })
            },

            clearForm() {
                this.form.id = null
                this.form.pid = null
                this.form.name = null
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
                            return this.type === 'add' ? addDepartment(this.form) : updateDepartment(this.form)
                        })
                        .then(({msg}) => {
                            elSuccess(msg)
                            this.$emit('success')
                            this.cancel()
                        })
                        .finally(() => this.loading = false)
                })
            }
        }
    }
</script>
