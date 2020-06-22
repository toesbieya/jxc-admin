<template>
    <transition :name="transition" @after-leave="handleAfterLeave">
        <div
                v-show="visible"
                class="puzzle-container"
                :class="{'puzzle-shake':stat==='fail'}"
                :style="`left: ${positionLeft}px;top: ${positionTop}px;`"
        >
            <div class="puzzle-view" :style="`width:${width}px;height:${height}px;`">
                <div v-show="!image.loading" style="width: 100%;height: 100%">
                    <img :src="image.src"/>

                    <canvas ref="puzzleBox" :height="height" :width="width"/>

                    <div class="puzzle-lost-box" :style="`left:${leftNum}px`">
                        <canvas ref="puzzleShadow" :style="sliderStyle" :height="height" :width="width"/>
                        <canvas ref="puzzleLost" :style="sliderStyle" :height="height" :width="width"/>
                    </div>
                </div>

                <transition name="fade">
                    <div v-show="image.loading" class="puzzle-image-loading">
                        <div class="puzzle-image-loading__icon"/>
                        <div class="puzzle-image-loading__text">加载中...</div>
                    </div>
                </transition>

                <div :class="resultTipClass">
                    {{stat==='success'?'验证通过':'请正确拼合图像'}}
                </div>
            </div>

            <div class="slider-container">
                <div class="slider-track">
                    <div class="slider-tip" :style="stat==='ready'?'opacity: 1':''">拖动滑块完成拼图</div>
                </div>
                <div class="slider-btn" :style="sliderBtnStyle" @mousedown="moveStart" @touchstart="moveStart"/>
            </div>

            <div class="puzzle-footer">
                <div class="puzzle-small">
                    <a class="puzzle-text-container puzzle-close" @click="close">
                        <div class="puzzle-text-tip">关闭验证</div>
                    </a>

                    <a class="puzzle-text-container puzzle-refresh" @click="refresh">
                        <div class="puzzle-text-tip">刷新验证</div>
                    </a>
                </div>
            </div>
        </div>
    </transition>
</template>

<script>
    import BtnStopImage from './image/btn_stop.png'
    import BtnMoveImage from './image/btn_move.png'

    /*
    * https://github.com/Kevin-269581661/vue-puzzle-verification
    * 网上搬的例子，实际开发建议用极验
    * */
    export default {
        name: "PuzzleVerify",

        props: {
            //动画名称，具体请查看transition.scss
            transition: {
                type: String,
                default: 'scale'
            },
            // 画布图片的尺寸
            width: {
                type: Number,
                default: 260
            },
            height: {
                type: Number,
                default: 160
            },
            // 滑块的大小
            blockSize: {
                type: Number,
                default: 40
            },
            // 误差
            deviation: {
                type: Number,
                default: 4
            },
            // 滑块随机出现的范围
            padding: {
                type: Number,
                default: 80
            }
        },

        data() {
            return {
                visible: false,
                stat: 'ready',//ready,move,success,fail,error
                positionLeft: 0,
                positionTop: 0,
                resolve: () => ({}),
                reject: () => ({}),
                moveStartAtX: null,
                randomX: null,
                randomY: null,
                leftNum: 0,
                image: {
                    src: '',
                    loading: true
                },
                sliderStyle: {
                    left: '0',
                    transition: 'inherit'
                }
            }
        },

        computed: {
            resultTipClass() {
                const showResultTip = ['success', 'fail'].includes(this.stat)
                return ['verify-result', `verify-${this.stat}`, showResultTip ? 'show-result' : '']
            },

            sliderBtnStyle() {
                return {
                    ...this.sliderStyle,
                    'background-image': `url(${this.stat === 'move' ? BtnMoveImage : BtnStopImage})`
                }
            }
        },

        methods: {
            close() {
                this.visible = false
                this.clear()
                this.reject()
            },

            refresh() {
                this.initImage().then(() => this.initCanvas())
            },

            initCanvas() {
                this.clear()
                let MinX = this.padding + this.blockSize
                let MaxX = this.width - this.padding - 2 * this.blockSize - this.blockSize / 6
                let MaxY = this.padding
                let MinY = this.height - this.padding - this.blockSize - this.blockSize / 6
                this.randomX = Math.round(Math.random() * MaxX + MinX)
                this.randomY = Math.round(Math.random() * MaxY + MinY)
                let X = this.randomX
                let Y = this.randomY
                this.leftNum = -X + 10
                let d = this.blockSize / 3

                let c = this.$refs.puzzleBox
                let c_l = this.$refs.puzzleLost
                let c_s = this.$refs.puzzleShadow
                let ctx = c.getContext("2d")
                let ctx_l = c_l.getContext("2d")
                let ctx_s = c_s.getContext("2d")
                ctx.globalCompositeOperation = "xor"
                ctx.shadowBlur = 10
                ctx.shadowColor = "#fff"
                ctx.shadowOffsetX = 3
                ctx.shadowOffsetY = 3
                ctx.fillStyle = "rgba(0,0,0,0.7)"
                ctx.beginPath()
                ctx.lineWidth = 1
                ctx.strokeStyle = "rgba(0,0,0,0)"

                ctx.moveTo(X, Y)
                ctx.lineTo(X + d, Y)
                ctx.bezierCurveTo(X + d, Y - d, X + 2 * d, Y - d, X + 2 * d, Y)
                ctx.lineTo(X + 3 * d, Y)
                ctx.lineTo(X + 3 * d, Y + d)
                ctx.bezierCurveTo(X + 2 * d, Y + d, X + 2 * d, Y + 2 * d, X + 3 * d, Y + 2 * d)
                ctx.lineTo(X + 3 * d, Y + 3 * d)
                ctx.lineTo(X, Y + 3 * d)

                ctx.closePath()
                ctx.stroke()
                ctx.fill()

                this.drawImage(img => ctx_l.drawImage(img, 0, 0, this.width, this.height))

                ctx_l.beginPath()
                ctx_l.strokeStyle = "rgba(0,0,0,0)"

                ctx_l.moveTo(X, Y)
                ctx_l.lineTo(X + d, Y)
                ctx_l.bezierCurveTo(X + d, Y - d, X + 2 * d, Y - d, X + 2 * d, Y)
                ctx_l.lineTo(X + 3 * d, Y)
                ctx_l.lineTo(X + 3 * d, Y + d)
                ctx_l.bezierCurveTo(X + 2 * d, Y + d, X + 2 * d, Y + 2 * d, X + 3 * d, Y + 2 * d)
                ctx_l.lineTo(X + 3 * d, Y + 3 * d)
                ctx_l.lineTo(X, Y + 3 * d)

                ctx_l.closePath()
                ctx_l.stroke()
                ctx_l.shadowBlur = 10
                ctx_l.shadowColor = "black"
                ctx_l.clip()
                ctx_s.beginPath()
                ctx_s.lineWidth = 1
                ctx_s.strokeStyle = "rgba(0,0,0,0)"

                ctx_s.moveTo(X, Y)
                ctx_s.lineTo(X + d, Y)
                ctx_s.bezierCurveTo(X + d, Y - d, X + 2 * d, Y - d, X + 2 * d, Y)
                ctx_s.lineTo(X + 3 * d, Y)
                ctx_s.lineTo(X + 3 * d, Y + d)
                ctx_s.bezierCurveTo(X + 2 * d, Y + d, X + 2 * d, Y + 2 * d, X + 3 * d, Y + 2 * d)
                ctx_s.lineTo(X + 3 * d, Y + 3 * d)
                ctx_s.lineTo(X, Y + 3 * d)

                ctx_s.closePath()
                ctx_s.stroke()
                ctx_s.shadowBlur = 20
                ctx_s.shadowColor = "black"
                ctx_s.fill()
            },

            initImage() {
                const imageListTemp = [
                    "https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=4018557288,1217151095&fm=26&gp=0.jpg",
                    "https://dss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3252521864,872614242&fm=26&gp=0.jpg",
                    "https://dss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2534506313,1688529724&fm=26&gp=0.jpg"
                ]
                this.image.loading = true
                let imgRandomIndex = Math.round(Math.random() * (imageListTemp.length - 1))
                this.image.src = imageListTemp[imgRandomIndex]
                return new Promise(resolve => resolve())
            },

            drawImage(fun) {
                let img = new Image()
                img.src = this.image.src
                img.onload = () => {
                    fun(img)
                    this.image.loading = false
                }
            },

            clear() {
                this.stat = 'ready'
                let c = this.$refs.puzzleBox
                let c_l = this.$refs.puzzleLost
                let c_s = this.$refs.puzzleShadow
                c.setAttribute("height", c.getAttribute("height"))
                c_l.setAttribute("height", c.getAttribute("height"))
                c_s.setAttribute("height", c.getAttribute("height"))
            },

            moveStart(e) {
                if (this.stat !== 'ready') return
                e.preventDefault()
                e.stopPropagation()
                this.moveStartAtX = e.type === 'mousedown' ? e.pageX : e.changedTouches[0].pageX
                this.addListener()
            },

            moving(e) {
                this.stat = 'move'
                const pageX = e.type === 'mousemove' ? e.pageX : e.changedTouches[0].pageX
                const distance = pageX - this.moveStartAtX
                if (this.moveStartAtX === null
                    || distance < 0
                    || distance > this.width - this.blockSize) {
                    return
                }
                this.sliderStyle.left = distance + "px"
                this.sliderStyle.transition = "inherit"
            },

            moveEnd(e) {
                const pageX = e.type === 'mouseup' ? e.pageX : e.changedTouches[0].pageX
                const distance = pageX - this.moveStartAtX
                const ver_Num = this.randomX - 10
                const minLeft = ver_Num - this.deviation
                const maxLeft = ver_Num + this.deviation
                if (this.moveStartAtX !== null) {
                    const success = maxLeft > distance && distance > minLeft
                    this.stat = success ? 'success' : 'fail'
                    setTimeout(() => {
                        this.stat = 'ready'
                        if (!success) return this.initCanvas()
                        this.resolve()
                        this.close()
                    }, success ? 500 : 1000)
                }
                setTimeout(() => {
                    this.sliderStyle.left = '0'
                    this.sliderStyle.transition = "left 0.5s"
                }, 1000)
                this.moveStartAtX = null
                this.removeListener()
            },

            addListener() {
                if (this.image.loading) return
                document.addEventListener("mousemove", this.moving)
                document.addEventListener("mouseup", this.moveEnd)
                document.addEventListener("touchmove", this.moving)
                document.addEventListener("touchend", this.moveEnd)
            },

            removeListener() {
                document.removeEventListener("mousemove", this.moving)
                document.removeEventListener("mouseup", this.moveEnd)
                document.addEventListener("touchmove", this.moving)
                document.addEventListener("touchend", this.moveEnd)
            },

            handleAfterLeave() {
                this.$destroy(true)
                this.$el.parentNode.removeChild(this.$el)
            }
        },

        mounted() {
            this.initImage().then(() => this.initCanvas())
        },
    }
</script>

<style lang="scss" src="./style.scss"></style>
