<template functional>
    <div class="error-container">
        <div>
            <div class="pic">
                <img alt="403" height="428" src="@/assets/images/403.gif" width="313">
            </div>
            <div class="bullshit">
                <div class="bullshit__oops">Oops!</div>
                <div class="bullshit__headline">没有访问权限</div>
                <div class="bullshit__info">
                    请联系管理员，或者点击下方按钮返回
                </div>
                <router-link class="bullshit__return-home" to="/index">返回首页</router-link>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'Page403'
    }
</script>

<style lang="scss" scoped>
    .error-container {
        transform: translate(-50%, -50%);
        position: absolute;
        top: 40%;
        left: 50%;

        > div {
            position: relative;
            width: 1200px;
            padding: 0 50px;
            overflow: hidden;

            .pic {
                position: relative;
                float: left;
                width: 600px;
                overflow: hidden;
            }

            .bullshit {
                position: relative;
                float: left;
                width: 300px;
                padding: 30px 0;
                overflow: hidden;

                &__oops {
                    font-size: 32px;
                    font-weight: bold;
                    line-height: 40px;
                    color: #1482f0;
                    opacity: 0;
                    margin-bottom: 20px;
                    animation-name: slideUp;
                    animation-duration: 0.5s;
                    animation-fill-mode: forwards;
                }

                &__headline {
                    font-size: 20px;
                    line-height: 24px;
                    color: #222;
                    font-weight: bold;
                    opacity: 0;
                    margin-bottom: 10px;
                    animation-name: slideUp;
                    animation-duration: 0.5s;
                    animation-delay: 0.1s;
                    animation-fill-mode: forwards;
                }

                &__info {
                    font-size: 13px;
                    line-height: 21px;
                    color: grey;
                    opacity: 0;
                    margin-bottom: 30px;
                    animation-name: slideUp;
                    animation-duration: 0.5s;
                    animation-delay: 0.2s;
                    animation-fill-mode: forwards;
                }

                &__return-home {
                    display: block;
                    float: left;
                    width: 110px;
                    height: 36px;
                    background: #1482f0;
                    border-radius: 100px;
                    text-align: center;
                    color: #ffffff;
                    opacity: 0;
                    font-size: 14px;
                    line-height: 36px;
                    cursor: pointer;
                    animation-name: slideUp;
                    animation-duration: 0.5s;
                    animation-delay: 0.3s;
                    animation-fill-mode: forwards;
                }

                @keyframes slideUp {
                    0% {
                        transform: translateY(60px);
                        opacity: 0;
                    }
                    100% {
                        transform: translateY(0);
                        opacity: 1;
                    }
                }
            }
        }
    }
</style>
