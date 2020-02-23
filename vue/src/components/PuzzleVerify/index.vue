<template>
    <div class="puzzle-container">
        <div class="puzzle-header">
            <span class="puzzle-header-title">拖动下方滑块完成拼图</span>
            <div class="puzzle-header-tool">
                <el-button @click="refreshImg" icon="el-icon-refresh" size="small" type="text"/>
                <el-button @click="$emit('close')" icon="el-icon-close" size="small" type="text"/>
            </div>
        </div>

        <div :style="'width:' + width + 'px'" class="puzzle-view">
            <img :src="imgRandom"
                 :style="'width:' + width + 'px;height:' + height + 'px;'"
                 id="scream"
                 ref="scream"
            />
            <canvas :height="height" :width="width" id="puzzle-box" ref="puzzleBox"/>
            <div :style="'left:' + left_Num+ 'px'" class="puzzle-lost-box">
                <canvas :height="height" :width="width" id="puzzle-shadow" ref="puzzleShadow"/>
                <canvas :height="height" :width="width" id="puzzle-lost" ref="puzzleLost"/>
            </div>
            <span :class="{'hide':displayTips}" class="verify-result" ref="verTips">
                <span class="success" v-show="verification">
                    <i class="el-icon-circle-check"/>验证通过
                </span>
                <span class="error" v-show="!verification">
                    <i class="el-icon-circle-close"/>验证失败
                </span>
            </span>
        </div>

        <div class="slider-container">
            <div class="slider-bar"/>
            <el-button @mousedown.native.prevent.stop="startMove"
                       circle
                       class="slider-btn"
                       icon="el-icon-rank"
                       ref="sliderBtn"
                       type="success"/>
        </div>
    </div>
</template>

<script>
    /*
    * https://github.com/Kevin-269581661/vue-puzzle-verification
    * 网上搬的例子，实际开发建议用极验
    * */
    export default {
        name: "PuzzleVerify",
        props: {
            // 画布图片的尺寸
            width: {
                type: Number,
                default: 260
            },
            height: {
                type: Number,
                default: 120
            },
            // 图集
            puzzleImgList: {
                type: Array,
                default: () => []
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
                default: 20
            }
        },
        data() {
            return {
                moveStart: null,
                displayTips: false,
                verification: false,
                randomX: null,
                randomY: null,
                imgRandom: "",
                left_Num: 0
            }
        },
        methods: {
            //刷新
            refreshImg() {
                let imgRandomIndex = Math.round(Math.random() * (this.puzzleImgList.length - 1))
                this.imgRandom = this.puzzleImgList[imgRandomIndex]
                this.initCanvas()
            },
            //画布初始化
            initCanvas() {
                this.clearCanvas()
                let w = this.width
                let h = this.height
                let PL_Size = this.blockSize
                let padding = this.padding
                let MinN_X = padding + PL_Size
                let MaxN_X = w - padding - PL_Size - PL_Size / 6
                let MaxN_Y = padding
                let MinN_Y = h - padding - PL_Size - PL_Size / 6
                this.randomX = Math.round(Math.random() * (MaxN_X - PL_Size) + MinN_X)
                this.randomY = Math.round(Math.random() * MaxN_Y + MinN_Y)
                let X = this.randomX
                let Y = this.randomY
                this.left_Num = -X + 10
                let d = PL_Size / 3

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

                let img = new Image()
                img.src = this.imgRandom

                img.onload = function () {
                    ctx_l.drawImage(img, 0, 0, w, h)
                }
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
            //通过重置画布尺寸清空画布，这种方式更彻底
            clearCanvas() {
                let c = this.$refs.puzzleBox
                let c_l = this.$refs.puzzleLost
                let c_s = this.$refs.puzzleShadow
                c.setAttribute("height", c.getAttribute("height"))
                c_l.setAttribute("height", c.getAttribute("height"))
                c_s.setAttribute("height", c.getAttribute("height"))
            },
            //按住滑块后初始化移动监听，记录初始位置
            startMove(e) {
                this.moveStart = e.pageX
                this.addMouseMoveListener()
            },
            //滑块移动
            moving(e) {
                let moveX = e.pageX
                let d = moveX - this.moveStart
                if (this.moveStart === null
                    || d < 0
                    || d > this.width - this.padding - this.blockSize) return
                this.$refs.sliderBtn.$el.style.left = d + "px"
                this.$refs.sliderBtn.$el.style.transition = "inherit"
                this.$refs.puzzleLost.style.left = d + "px"
                this.$refs.puzzleLost.style.transition = "inherit"
                this.$refs.puzzleShadow.style.left = d + "px"
                this.$refs.puzzleShadow.style.transition = "inherit"
            },
            //移动结束，验证并回调
            moveEnd(e) {
                let moveEnd_X = e.pageX - this.moveStart
                let ver_Num = this.randomX - 10
                let Min_left = ver_Num - this.deviation
                let Max_left = ver_Num + this.deviation
                if (this.moveStart !== null) {
                    if (Max_left > moveEnd_X && moveEnd_X > Min_left) {
                        this.displayTips = true
                        this.verification = true
                        setTimeout(() => {
                            this.displayTips = false
                            this.initCanvas()
                            this.$emit('success')
                        }, 500)
                    }
                    else {
                        this.displayTips = true
                        this.verification = false
                        setTimeout(() => {
                            this.displayTips = false
                            this.initCanvas()
                            this.$emit('fail')
                        }, 800)
                    }
                }
                setTimeout(() => {
                    this.$refs.sliderBtn.$el.style.left = '0'
                    this.$refs.sliderBtn.$el.style.transition = "left 0.5s"
                    this.$refs.puzzleLost.style.left = '0'
                    this.$refs.puzzleLost.style.transition = "left 0.5s"
                    this.$refs.puzzleShadow.style.left = '0'
                    this.$refs.puzzleShadow.style.transition = "left 0.5s"
                }, 400)
                this.moveStart = null
                this.removeMouseMoveListener()
            },
            //全局绑定滑块移动与滑动结束，移动过程中鼠标可在页面任何位置
            addMouseMoveListener() {
                document.addEventListener("mousemove", this.moving)
                document.addEventListener("mouseup", this.moveEnd)
            },
            removeMouseMoveListener() {
                document.removeEventListener("mousemove", this.moving)
                document.removeEventListener("mouseup", this.moveEnd)
            }
        },
        created() {
            // 随机显示一张图片
            let imgRandomIndex = Math.round(Math.random() * (this.puzzleImgList.length - 1))
            this.imgRandom = this.puzzleImgList[imgRandomIndex]
        },
        mounted() {
            this.$nextTick(() => this.initCanvas())
        },

    }
</script>

<style lang="scss">
    .puzzle-container {
        position: relative;
        display: inline-block;
        padding: 0 15px 28px;
        border: 1px solid #ddd;
        background: #ffffff;
        border-radius: 10px;

        .puzzle-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin: 5px 0;

            .puzzle-header-title {
                font-size: 13px;
                color: #909399
            }
        }

        .puzzle-view {
            position: relative;
            overflow: hidden;

            .puzzle-lost-box {
                position: absolute;
                left: 0;
                top: 0;
            }

            canvas {
                position: absolute;
                left: 0;
                top: 0;
                z-index: 22;
            }

            .verify-result {
                position: absolute;
                left: 0;
                bottom: -22px;
                background: rgba(255, 255, 255, 1);
                height: 22px;
                line-height: 22px;
                font-size: 12px;
                width: 100%;
                transition: bottom 0.4s;

                .success {
                    color: #67C23A
                }

                .error {
                    color: #F56C6C
                }

                i {
                    margin: 0 10px;
                    font-weight: bold;
                }

                &.hide {
                    bottom: 0;
                }
            }
        }

        .slider-container {
            position: relative;
            margin: 10px auto 0;
            min-height: 15px;

            .slider-bar {
                height: 10px;
                border: 1px solid #c3c3c3;
                border-radius: 5px;
                background: #e4e4e4;
                box-shadow: 0 1px 1px rgba(12, 10, 10, 0.2) inset;
                position: relative;
                top: 7px;
            }

            .slider-btn {
                position: absolute;
                width: 44px;
                height: 44px;
                left: 0;
                top: -10px;
                z-index: 12;
                transition: inherit;
            }
        }
    }
</style>
