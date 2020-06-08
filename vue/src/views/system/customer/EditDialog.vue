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
            <el-form-item label="名 称：" prop="name">
                <el-input v-model="form.name" :readonly="!canEdit" maxlength="20"/>
            </el-form-item>
            <el-form-item label="行政区域：" prop="region_name">
                <region-selector
                        :value="form.region_name"
                        get-children-on-click
                        readonly
                        @select="selectRegion"
                />
            </el-form-item>
            <el-form-item label="地 址：" prop="address">
                <el-input v-model="form.address" :readonly="!canEdit" maxlength="100"/>
            </el-form-item>
            <el-form-item label="联系人：" prop="linkman">
                <el-input v-model="form.linkman" :readonly="!canEdit" maxlength="50"/>
            </el-form-item>
            <el-form-item label="联系电话：" prop="linkphone">
                <el-input v-model="form.linkphone" :readonly="!canEdit" maxlength="20"/>
            </el-form-item>
            <el-form-item label="状 态：" prop="status">
                <el-radio-group v-model="form.status" :disabled="!canEdit">
                    <el-radio :label="1">启用</el-radio>
                    <el-radio :label="0">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="备 注：" prop="remark">
                <el-input
                        v-model="form.remark"
                        :rows="4"
                        :readonly="!canEdit"
                        maxlength="200"
                        show-word-limit
                        type="textarea"
                />
            </el-form-item>
        </el-form>
        <template v-slot:footer>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canEdit" size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </dialog-form>
</template>

<script>
    import DialogForm from '@/components/DialogForm'
    import RegionSelector from '@/components/RegionSelector/Tree'
    import dialogMixin from "@/mixins/dialogMixin"
    import {addCustomer, updateCustomer} from "@/api/system/customer"
    import {isEmpty, mergeObj, resetObj} from '@/utils'
    import {elConfirm} from "@/utils/message"

    export default {
        name: "EditDialog",
        mixins: [dialogMixin],
        components: {DialogForm, RegionSelector},
        props: {
            value: {type: Boolean, default: false},
            type: {type: String, default: 'see'},
            data: {
                type: Object,
                default: () => ({})
            },
        },
        data() {
            return {
                loading: false,
                form: {
                    id: null,
                    name: null,
                    address: null,
                    linkman: null,
                    linkphone: null,
                    region: null,
                    region_name: null,
                    status: 1,
                    remark: null
                },
                rules: {
                    name: [{required: true, message: '名称不能为空', trigger: 'change'}],
                    address: [{required: true, message: '地址不能为空', trigger: 'change'}],
                    linkman: [{required: true, message: '联系人不能为空', trigger: 'change'}],
                    linkphone: [{required: true, message: '联系电话不能为空', trigger: 'change'}],
                    status: [{required: true, message: '状态不能为空', trigger: 'change'}],
                    region_name: [{required: true, message: '行政区域不能为空', trigger: 'change'}],
                }
            }
        },
        computed: {
            title() {
                if (isEmpty(this.type)) return ''
                switch (this.type) {
                    case 'see':
                        return '客户信息'
                    case 'add':
                        return '添加客户'
                    case 'edit':
                        return '编辑客户'
                }
            },
            confirmMessage() {
                switch (this.type) {
                    case 'add':
                        return `确认添加新的客户【${this.form.name}】?`
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
            selectRegion(obj) {
                this.form.region = obj.id
                this.form.region_name = obj.fullname
            },
            open() {
                if (this.type !== 'add') mergeObj(this.form, this.data)
            },
            clearForm() {
                resetObj(this.form)
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
                            return this.type === 'add' ? addCustomer(this.form) : updateCustomer(this.form)
                        })
                        .then(({msg}) => this.$emit('success', msg))
                        .finally(() => this.loading = false)
                })
            }
        }
    }
</script>
