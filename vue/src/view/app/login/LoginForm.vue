<template>
    <el-form ref="form" :model="form" :rules="rules" label-position="left">
        <el-form-item prop="username">
            <span class="svg-container">
                <v-icon icon="svg-user"/>
            </span>

            <el-input
                ref="username"
                v-model="form.username"
                :maxlength="20"
                name="username"
                placeholder="请输入用户名"
                type="text"
            />
        </el-form-item>

        <el-form-item prop="password">
            <el-tooltip v-model="capsTooltip" :tabindex="-1" content="大写锁定已打开" manual placement="left">
                <span class="svg-container">
                    <v-icon icon="svg-password"/>
                </span>
            </el-tooltip>

            <el-input
                ref="password"
                v-model="form.password"
                :key="passwordType"
                :type="passwordType"
                :maxlength="20"
                name="password"
                placeholder="请输入密码"
                @keyup.enter.native="login"
            />

            <span @click="showPwd" class="show-pwd">
                <v-icon :icon="passwordType === 'password' ? 'svg-eye' : 'svg-eye-open'"/>
            </span>
        </el-form-item>

        <el-button :loading="loading" class="submit-btn" type="primary" @click="login">
            {{ loading ? '登 录 中...' : '登 录' }}
        </el-button>

        <div class="flex" style="margin-top: 20px">
            <p class="other-ways">
                其他方式登录
                <v-icon v-for="i in otherWays" :key="i" :icon="`svg-${i}`" @click="() => thirdPartyLogin(i)"/>
            </p>

            <el-button type="text" @click="register">注册账户</el-button>
        </div>
    </el-form>
</template>

<script>
import {isEmpty} from "@/util"
import {elSuccess} from "@/util/message"
import {md5} from "@/util/secret"

export default {
    name: "LoginForm",

    data() {
        return {
            loading: false,
            form: {
                username: '',
                password: ''
            },
            rules: {
                username: [{required: true, message: '请输入用户名', trigger: 'change'}],
                password: [{required: true, message: '请输入密码', trigger: 'change'}]
            },
            passwordType: 'password',
            capsTooltip: false,
            otherWays: ['qq']
        }
    },

    methods: {
        showPwd() {
            this.passwordType = this.passwordType === 'password' ? '' : 'password'
            this.$nextTick(() => this.$refs.password.focus())
        },

        login() {
            if (this.loading) return
            this.$refs.form.validate(valid => {
                if (!valid) return
                this.loading = true
                this.$verify()
                    .then(() => this.$store.dispatch('user/login', {
                        ...this.form,
                        password: md5(this.form.password)
                    }))
                    .then(this.success)
                    .catch(() => this.loading = false)
            })
        },

        register() {
            !this.loading && this.$router.push('/register')
        },

        success() {
            elSuccess('登录成功')
            const redirect = this.$route.query.redirect || '/'
            //由于清除消息时会造成卡顿，所以延迟0.2s跳转
            window.setTimeout(() => this.$router.replace(redirect), 200)
        },

        thirdPartyLogin(channel) {
            this.$message.info('假装可以第三方登录')
        },

        capsLockTip({keyCode}) {
            if (keyCode === 20) this.capsTooltip = !this.capsTooltip
        },

        addCapsLockEvent() {
            window.addEventListener('keyup', this.capsLockTip)
        },

        removeEvent() {
            window.removeEventListener('keyup', this.addCapsLockEvent)
        }
    },

    mounted() {
        this.addCapsLockEvent()
        const key = isEmpty(this.form.username) ? 'username' : 'password'
        this.$refs[key].focus()

        this.$once('hook:beforeDestroy', this.removeEvent)
    }
}
</script>
