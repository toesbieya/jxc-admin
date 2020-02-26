<template>
    <div class="login-container">
        <canvas id="login-background"/>
        <el-form ref="form" :model="form" :rules="rules" autocomplete="on" class="login-form" label-position="left">
            <div class="title-container">
                <h3 class="title">进销存管理系统</h3>
            </div>
            <el-form-item prop="username">
                <span class="svg-container">
                    <svg-icon icon="user"/>
                </span>
                <el-input
                        ref="username"
                        v-model="form.username"
                        :maxlength="20"
                        autocomplete="on"
                        name="username"
                        placeholder="请输入用户名"
                        type="text"
                />
            </el-form-item>
            <el-form-item prop="password">
                <el-tooltip v-model="capsTooltip" :tabindex="-1" content="大写锁定已打开" manual placement="left">
                    <span class="svg-container">
                        <svg-icon icon="password"/>
                    </span>
                </el-tooltip>
                <el-input
                        ref="password"
                        v-model="form.password"
                        :key="passwordType"
                        :type="passwordType"
                        :maxlength="20"
                        autocomplete="on"
                        name="password"
                        placeholder="请输入密码"
                        @keyup.enter.native="login"
                />
                <span @click="showPwd" class="show-pwd">
                    <svg-icon :icon="passwordType === 'password' ? 'eye' : 'eye-open'"/>
                </span>
            </el-form-item>
            <el-button :loading="loading" style="width: 100%" type="primary" @click="login">登录</el-button>
            <div class="flex" style="margin-top: 20px">
                <p class="other-ways">
                    其他方式登录
                    <span v-for="i in otherWays" :key="i" @click="$message.info('假装可以第三方登录')">
                        <svg-icon :icon="i"/>
                    </span>
                </p>
                <el-button type="text" @click="!loading&&$router.push('/register')">注册账户</el-button>
            </div>
        </el-form>
    </div>
</template>

<script>
    import {loginBackgroundAnimate} from '@/config'
    import {isEmpty} from "@/utils"
    import md5 from "js-md5"
    import {elSuccess} from "@/utils/message"

    export default {
        name: 'login',
        data() {
            return {
                form: {
                    username: '',
                    password: ''
                },
                rules: {
                    username: [{required: true, message: '请输入用户名', trigger: 'change'}],
                    password: [
                        {required: true, message: '请输入密码', trigger: 'change'},
                        {min: 6, max: 32, message: '请输入6-32位的密码', trigger: 'change'}
                    ]
                },
                passwordType: 'password',
                capsTooltip: false,
                otherWays: ['qq'],
                loading: false,
                rain: null,
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
                    this.$store.dispatch('user/login', {...this.form, password: md5(this.form.password)})
                        .then(() => this.success())
                        .catch(() => this.loading = false)
                })
            },
            success() {
                elSuccess('登陆成功')
                let redirect = this.$route.query.redirect || '/'
                window.location.href = '/#' + redirect
            },
            capsLockTip({keyCode}) {
                if (keyCode === 20) this.capsTooltip = !this.capsTooltip
            },
            addCapsLockEvent() {
                document.addEventListener('keyup', this.capsLockTip)
            },
            removeEvent() {
                document.removeEventListener('keyup', this.addCapsLockEvent)
            }
        },
        mounted() {
            if (loginBackgroundAnimate) {
                import('@/plugin/rain')
                    .then(_ => {
                        this.rain = new _.default({
                            rainDropCount: 500,
                            rainColor: 'rgba(150,180,255,0.8)',
                            backgroundColor: '#2d3a4b'
                        }, document.getElementById('login-background'))
                    })
            }
            this.addCapsLockEvent()
            if (isEmpty(this.form.username)) this.$refs.username.focus()
            else this.$refs.password.focus()
        },
        beforeDestroy() {
            this.$message.closeAll()
            this.removeEvent()
            this.rain && this.rain.stop()
        }
    }
</script>

<style lang="scss">
    @import "src/assets/styles/login";

    .other-ways {
        color: #fff;
        font-size: 14px;

        span {
            cursor: pointer;
            margin-left: 10px;

            &:hover {
                color: $--color-primary;
            }
        }
    }
</style>
