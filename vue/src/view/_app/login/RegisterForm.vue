<template>
    <el-form ref="form" :model="form" :rules="rules" label-position="left">
        <el-form-item prop="username">
            <span class="svg-container">
                <v-icon icon="svg-user"/>
            </span>
            <el-input ref="username" v-model="form.username" :maxlength="20" placeholder="请输入登录名称"/>
        </el-form-item>

        <el-form-item prop="nick">
            <span class="svg-container">
                <v-icon icon="svg-user"/>
            </span>
            <el-input v-model="form.nick" :maxlength="100" placeholder="请输入昵称"/>
        </el-form-item>

        <el-form-item prop="pwd">
            <el-tooltip v-model="capsTooltip" :tabindex="-1" content="大写锁定已打开" manual placement="left">
                <span class="svg-container">
                    <v-icon icon="svg-password"/>
                </span>
            </el-tooltip>
            <el-input v-model="form.pwd" placeholder="请输入密码" type="password" :maxlength="20"/>
        </el-form-item>

        <el-form-item prop="repwd">
            <span class="svg-container">
                <v-icon icon="svg-password"/>
            </span>
            <el-input
                v-model="form.repwd"
                placeholder="请确认密码"
                type="password"
                :maxlength="20"
                @keyup.enter.native="register"
            />
        </el-form-item>

        <el-button
            :loading="loading"
            class="submit-btn"
            type="primary"
            @click="register"
        >
            {{ loading ? '注 册 中...' : '注 册' }}
        </el-button>

        <div class="flex" style="margin-top: 20px">
            <p/>
            <el-button type="text" @click="login">已有账户登录</el-button>
        </div>
    </el-form>
</template>

<script>
import {register, checkLoginName, checkNickName} from "@/api/account"
import {debounce} from "@/util"
import {elSuccess} from "@/util/message"
import {md5} from "@/util/secret"

export default {
    name: "RegisterForm",

    data() {
        const validateLoginName = debounce((r, v, c) => {
            checkLoginName
                .request(this.form.username, this.form.id)
                .then(({msg}) => msg ? c(msg) : c())
                .catch(e => c(e))
        }, 300)
        const validateNickName = debounce((r, v, c) => {
            checkNickName
                .request(this.form.nick, this.form.id)
                .then(({msg}) => msg ? c(msg) : c())
                .catch(e => c(e))
        }, 300)
        const validateRepwd = (r, v, c) => {
            return v !== this.form.pwd ? c('两次密码输入不一致') : c()
        }
        return {
            form: {
                username: '',
                nick: '',
                pwd: '',
                repwd: ''
            },
            rules: {
                username: [
                    {required: true, message: '请输入登录名称', trigger: 'change'},
                    {validator: validateLoginName, trigger: 'change'}
                ],
                nick: [
                    {required: true, message: '请输入昵称', trigger: 'change'},
                    {validator: validateNickName, trigger: 'change'}
                ],
                pwd: [
                    {required: true, message: '请输入密码', trigger: 'change'},
                    {min: 6, max: 32, message: '请输入6-32位的密码', trigger: 'change'}
                ],
                repwd: [
                    {required: true, message: '请确认密码', trigger: 'change'},
                    {validator: validateRepwd, trigger: 'change'}
                ],
            },
            capsTooltip: false,
            loading: false
        }
    },

    methods: {
        register() {
            if (this.loading) return
            this.$refs.form.validate(valid => {
                if (!valid) return
                this.loading = true
                register
                    .request({
                        username: this.form.username,
                        nick: this.form.nick,
                        password: md5(this.form.pwd)
                    })
                    .then(() => {
                        elSuccess('注册成功')
                        this.$router.push('/login')
                    })
                    .catch(() => this.loading = false)
            })
        },

        login() {
            !this.loading && this.$router.push('/login')
        },

        capsLockTip({keyCode}) {
            if (keyCode === 20) this.capsTooltip = !this.capsTooltip
        },

        addEvent() {
            window.addEventListener('keyup', this.capsLockTip)
        },

        removeEvent() {
            window.removeEventListener('keyup', this.addCapsLockEvent)
        }
    },

    mounted() {
        this.addEvent()
        this.$nextTick(() => this.$refs.username.focus())

        this.$once('hook:beforeDestroy', this.removeEvent)
    }
}
</script>
