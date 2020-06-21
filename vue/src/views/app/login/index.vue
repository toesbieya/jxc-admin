<template>
    <div class="login-page">
        <canvas id="login-background"/>

        <div class="login-container" @click.stop>
            <div class="title">
                {{title}}
                <set-animation :value="animation" custom-class="set-animation" @select="setAnimation"/>
            </div>

            <component :is="component"/>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import {title} from '@/config'
    import SetAnimation from "./SetAnimation"
    import LoginForm from "./LoginForm"
    import RegisterForm from "./RegisterForm"
    import {isEmpty} from "@/utils"

    export default {
        name: 'login',

        components: {LoginForm, RegisterForm, SetAnimation},

        data() {
            return {
                title,
                animationInstance: null
            }
        },

        computed: {
            ...mapState('app', {
                device: state => state.device,
                animation: state => state.loginBackgroundAnimation
            }),

            component() {
                const formType = this.$route.path.substring(1)
                return `${[...formType].join('')}-form`
            }
        },

        methods: {
            clearAnimation() {
                if (this.animationInstance) {
                    this.animationInstance.stop()
                    this.animationInstance = null
                }
            },

            setAnimation(value) {
                this.clearAnimation()
                this.$store.commit('app/loginBackgroundAnimation', value)
                if (isEmpty(value)) return
                import(`@/plugin/canvasAnimation/${value}`)
                    .then(_ => this.animationInstance = new _.default(document.getElementById('login-background')))
            }
        },

        mounted() {
            //移动端关闭动画，太卡
            this.device !== 'mobile' && this.setAnimation(this.animation)
        },

        beforeDestroy() {
            this.clearAnimation()
            this.$message.closeAll()
        }
    }
</script>

<style lang="scss" src="./style.scss"></style>
