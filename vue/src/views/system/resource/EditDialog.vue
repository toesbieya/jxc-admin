<template>
    <dialog-form :loading="loading" title="编辑" :value="value" @close="cancel" @open="open">
        <el-form
                ref="form"
                :model="form"
                :rules="rules"
                label-position="right"
                label-width="120px"
                status-icon
        >
            <el-form-item label="名 称：" prop="name">
                <el-input :value="form.fullName" readonly/>
            </el-form-item>
            <el-form-item label="访问路径：" prop="url">
                <el-input :value="form.url" readonly/>
            </el-form-item>
            <el-form-item label="总频率：" prop="total_rate">
                <el-input-number v-model="form.total_rate" :min="1" :step="1" step-strictly size="small"/>
            </el-form-item>
            <el-form-item label="单个IP频率：" prop="ip_rate">
                <el-input-number v-model="form.ip_rate" :min="1" :step="1" step-strictly size="small"/>
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
    import {updateResource} from "@/api/system/resource"
    import {mergeObj, resetObj} from '@/utils'

    export default {
        name: "EditDialog",
        mixins: [dialogMixin],
        components: {DialogForm},
        props: {
            value: {type: Boolean, default: false},
            data: {type: Object, default: () => ({})},
        },
        data() {
            return {
                loading: false,
                form: {
                    id: null,
                    fullName: null,
                    url: null,
                    total_rate: 0,
                    ip_rate: 0
                },
                rules: {
                    total_rate: [{required: true, message: '总频率不能为空', trigger: 'change'}],
                    ip_rate: [{required: true, message: '单个ip的频率不能为空', trigger: 'change'}],
                }
            }
        },
        methods: {
            open() {
                this.$nextTick(() => mergeObj(this.form, this.data))
            },
            cancel() {
                this.closeDialog()
                resetObj(this.form)
                this.$nextTick(() => this.$refs.form.clearValidate())
            },
            confirm() {
                if (this.loading) return
                this.$refs.form.validate(v => {
                    if (!v) return
                    this.loading = true
                    updateResource(this.form)
                        .then(({msg}) => this.$emit('success', msg))
                        .finally(() => this.loading = false)
                })
            },
        }
    }
</script>
