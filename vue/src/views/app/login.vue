<template>
    <div class="login-page">
        <canvas id="login-background"/>
        <div class="login-container" @click.stop>
            <div class="title">
                进销存管理系统
                <set-animation
                        :value="loginPageBackgroundAnimation"
                        custom-class="set-animation"
                        @select="setAnimation"
                />
            </div>
            <el-form ref="form" :model="form" :rules="rules" autocomplete="on" label-position="left">
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
                <el-button :loading="loading" style="width: 100%" type="primary" @click="login">登 录</el-button>
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
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import md5 from "js-md5"
    import {isEmpty} from "@/utils"
    import {elSuccess} from "@/utils/message"
    import SetAnimation from "./components/SetAnimation"

    export default {
        name: 'login',
        components: {SetAnimation},
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
                animation: null
            }
        },
        computed: mapState('app', {
            device: state => state.device,
            loginPageBackgroundAnimation: state => state.loginPageBackgroundAnimation
        }),
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
                    this.$puzzleVerify()
                        .then(() => this.$store.dispatch('user/login', {
                            ...this.form,
                            password: md5(this.form.password)
                        }))
                        .then(() => this.success())
                        .catch(() => this.loading = false)
                })
            },
            success() {
                elSuccess('登陆成功')
                let redirect = this.$route.query.redirect || '/'
                //由于清除消息时会造成卡顿，所以延迟0.5s跳转
                setTimeout(() => window.location.href = '/#' + redirect, 500)
            },
            capsLockTip({keyCode}) {
                if (keyCode === 20) this.capsTooltip = !this.capsTooltip
            },
            addCapsLockEvent() {
                document.addEventListener('keyup', this.capsLockTip)
            },
            removeEvent() {
                document.removeEventListener('keyup', this.addCapsLockEvent)
            },
            clearAnimation() {
                if (this.animation) {
                    this.animation.stop()
                    this.animation = null
                }
            },
            setAnimation(value) {
                this.clearAnimation()
                this.$store.commit('app/loginPageBackgroundAnimation', value)
                if (isEmpty(value)) return
                import(`@/plugin/canvasAnimation/${value}`)
                    .then(_ => this.animation = new _.default(document.getElementById('login-background')))
            }
        },
        mounted() {
            //移动端关闭动画，太卡
            this.device !== 'mobile' && this.setAnimation(this.loginPageBackgroundAnimation)
            this.addCapsLockEvent()
            if (isEmpty(this.form.username)) this.$refs.username.focus()
            else this.$refs.password.focus()
        },
        beforeDestroy() {
            this.$message.closeAll()
            this.removeEvent()
            this.clearAnimation()
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
