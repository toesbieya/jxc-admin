<template>
    <dialog-form :loading="loading" title="修改密码" :value="value" width="30%" @close="cancel">
        <el-form
                ref="form"
                :model="form"
                :rules="rules"
                size="small"
                label-position="right"
                label-width="100px"
                status-icon
        >
            <el-form-item label="旧密码：" prop="old_pwd">
                <el-input v-model.trim="form.old_pwd" maxlength="100" type="password"/>
            </el-form-item>
            <el-form-item label="新密码：" prop="new_pwd">
                <el-input v-model.trim="form.new_pwd" maxlength="100" type="password"/>
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
    import {updateUserPwd} from "@/api/system/user"
    import {elSuccess} from "@/utils/message"

    export default {
        mixins: [dialogMixin],
        components: {DialogForm},
        data() {
            return {
                loading: false,
                form: {
                    old_pwd: null,
                    new_pwd: null
                },
                rules: {
                    old_pwd: [{required: true, message: '请输入原密码', trigger: 'change'}],
                    new_pwd: [
                        {required: true, message: '请输入新密码', trigger: 'change'},
                        {
                            validator: (r, v, c) => v !== this.form.old_pwd ? c() : c('新密码不得与旧密码相同'),
                            trigger: 'change'
                        },
                        {
                            min: 6,
                            max: 32,
                            message: '请输入6-32位的密码',
                            trigger: 'change'
                        }
                    ]
                }
            }
        },
        props: {
            value: Boolean
        },
        methods: {
            clearForm() {
                this.form.old_pwd = null
                this.form.new_pwd = null
                this.$nextTick(() => this.$refs.form.clearValidate())
            },
            confirm() {
                if (this.loading) return
                this.$refs.form.validate(v => {
                    if (!v) return
                    this.loading = true
                    updateUserPwd(this.form)
                        .then(() => elSuccess('修改成功'))
                        .finally(() => {
                            this.loading = false
                            this.closeDialog()
                        })
                })
            },
            cancel() {
                this.closeDialog()
                this.clearForm()
            }
        }
    }
</script>
