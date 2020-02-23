<template>
    <el-form ref="form" :model="form" :rules="rules" size="small" label-position="right" label-width="100px"
             status-icon>
        <el-form-item label="旧密码：" prop="old_pwd">
            <el-input v-model.trim="form.old_pwd" autocomplete="off" maxlength="100" type="password"/>
        </el-form-item>
        <el-form-item label="新密码：" prop="new_pwd">
            <el-input v-model.trim="form.new_pwd" autocomplete="off" maxlength="100" type="password"/>
        </el-form-item>
        <el-form-item>
            <el-button :loading="loading" type="primary" @click="submit">确认修改</el-button>
        </el-form-item>
    </el-form>
</template>

<script>
    import {updateUserPwd} from "@/api/system/user"
    import {elSuccess} from "@/utils/message"

    export default {
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
                            validator: (r, v, c) => {
                                v !== this.form.old_pwd ? c() : c('新密码不得与旧密码相同')
                            },
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
        methods: {
            clearForm() {
                this.form.old_pwd = null
                this.form.new_pwd = null
                this.$refs.form.resetFields()
            },
            submit() {
                if (this.loading) return
                this.$refs.form.validate(v => {
                    if (!v) return
                    this.loading = true
                    updateUserPwd(this.form)
                        .then(() => elSuccess('修改成功'))
                        .finally(() => {
                            this.loading = false
                            this.clearForm()
                        })
                })
            }
        }
    }
</script>
