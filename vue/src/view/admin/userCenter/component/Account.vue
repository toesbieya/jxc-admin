<template>
    <abstract-dialog :loading="loading" title="修改密码" :value="value" width="30%" @close="cancel">
        <abstract-form :model="form" :rules="rules">
            <el-form-item label="旧密码" prop="oldPwd">
                <el-input v-model.trim="form.oldPwd" maxlength="100" type="password"/>
            </el-form-item>
            <el-form-item label="新密码" prop="newPwd">
                <el-input v-model.trim="form.newPwd" maxlength="100" type="password"/>
            </el-form-item>
        </abstract-form>

        <template v-slot:footer>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </abstract-dialog>
</template>

<script>
import dialogMixin from "@/mixin/dialogMixin"
import AbstractForm from "@/component/abstract/Form"
import AbstractDialog from '@/component/abstract/Dialog'
import {updateUserPwd} from "@/api/account"
import {elSuccess} from "@/util/message"
import {md5} from "@/util/secret"

export default {
    mixins: [dialogMixin],

    components: {AbstractForm, AbstractDialog},

    data() {
        return {
            loading: false,
            form: {
                oldPwd: null,
                newPwd: null
            },
            rules: {
                oldPwd: [{required: true, message: '请输入原密码', trigger: 'change'}],
                newPwd: [
                    {required: true, message: '请输入新密码', trigger: 'change'},
                    {
                        validator: (r, v, c) => v !== this.form.oldPwd ? c() : c('新密码不得与旧密码相同'),
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
            this.form.oldPwd = null
            this.form.newPwd = null
            this.$nextTick(() => this.$refs.form.clearValidate())
        },

        confirm() {
            if (this.loading) return
            this.$refs.form.validate(v => {
                if (!v) return
                this.loading = true
                updateUserPwd
                    .request({
                        oldPwd: md5(this.form.oldPwd),
                        newPwd: md5(this.form.newPwd),
                    })
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
