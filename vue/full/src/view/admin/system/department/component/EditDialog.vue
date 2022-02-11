<template>
    <abstract-dialog :loading="loading" :title="title" :value="value" @close="cancel" @open="open">
        <abstract-form :model="form" :rules="rules">
            <el-form-item v-show="parentName" label="上级部门">
                <el-input :value="parentName" readonly/>
            </el-form-item>
            <el-form-item label="部门名称" prop="name">
                <el-input v-model="form.name" maxlength="20"/>
            </el-form-item>
            <el-form-item label="部门状态" prop="enable">
                <el-radio-group v-model="form.enable">
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
import AbstractDialog from '@/component/abstract/Dialog'
import {add, update} from "@/api/system/department"
import {isEmpty, mergeObj} from '@/util'
import {elConfirm, elSuccess} from "@/util/message"

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
                pid: null,
                name: null,
                enable: false,
            },
            rules: {
                name: [{required: true, message: '部门名称不能为空', trigger: 'change'}]
            }
        }
    },

    computed: {
        title() {
            if (!this.data || isEmpty(this.type)) return ''
            switch (this.type) {
                case 'add':
                    return '添加部门'
                case 'see':
                    return `查看部门【${this.data.name}】`
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

        canEdit() {
            return ['add', 'edit'].includes(this.type)
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
                if (this.type !== 'add') {
                    mergeObj(this.form, this.data)
                }
                else this.form.pid = this.data.id
            })
        },

        clearForm() {
            this.form.id = null
            this.form.pid = null
            this.form.name = null
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
                elConfirm(this.confirmMessage)
                    .then(() => {
                        this.loading = true
                        return this.type === 'add' ? add.request(this.form) : update.request(this.form)
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
