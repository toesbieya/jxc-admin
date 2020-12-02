<template>
    <div v-show="visible" class="signature-board">
        <canvas
            ref="canvas"
            @mousedown.prevent="drawStart"
            @mousemove.prevent="drawMove"
            @mouseup.prevent="drawEnd"
            @touchstart.prevent="drawStart"
            @touchmove.prevent="drawMove"
            @touchend.prevent="drawEnd"
        />

        <div class="signature-board__footer">
            <el-button plain size="small" @click="clear">清 空</el-button>
            <el-button plain size="small" @click="close">关 闭</el-button>
            <el-button size="small" type="primary" @click="confirm">确 定</el-button>
        </div>
    </div>
</template>

<script>
/*
* 突然发现没办法做到竖屏横屏都支持，坑爹
* */

function getEventPoint(e) {
    let x, y
    if (['mousedown', 'mousemove'].includes(e.type)) {
        x = e.clientX - e.target.offsetLeft
        y = e.clientY - e.target.offsetTop
    }
    else {
        x = e.changedTouches[0].clientX - e.target.offsetLeft
        y = e.changedTouches[0].clientY - e.target.offsetTop
    }
    return {x, y}
}

export default {
    name: 'SignatureBoard',

    props: {
        image: String,
        lineWidth: Number,
        lineColor: String,
        onConfirm: Function
    },

    data() {
        return {
            visible: false,
            drew: false,
            drawing: false,
            startX: 0,
            startY: 0
        }
    },

    methods: {
        drawStart(e) {
            const {x, y} = getEventPoint(e)
            this.drew = true
            this.drawing = true
            this.startX = x
            this.startY = y
            this.ctx.strokeStyle = this.lineColor
            this.ctx.lineWidth = this.lineWidth
        },

        drawMove(e) {
            if (!this.drawing) return
            const {x, y} = getEventPoint(e)
            this.ctx.beginPath()
            this.ctx.moveTo(this.startX, this.startY)
            this.ctx.lineTo(x, y)
            this.ctx.stroke()
            this.ctx.closePath()
            this.startY = y
            this.startX = x
        },

        drawEnd() {
            this.drawing = false
        },

        getBase64() {
            if (!this.drew) return null
            return this.$refs.canvas.toDataURL()
        },

        resize() {
            const canvas = this.$refs.canvas

            canvas.height = window.innerHeight
            canvas.width = window.innerWidth
        },

        init() {
            this.resize()

            this.ctx = this.$refs.canvas.getContext('2d')
            this.ctx.lineCap = 'round'
            this.ctx.lineJoin = 'round'

            if (this.image) {
                let img = new Image()
                img.src = this.image
                img.onload = () => {
                    this.ctx.drawImage(img, 0, 0, img.width, img.height)
                    this.drew = true
                    img = null
                }
            }
        },

        clear() {
            this.drew = false
            this.drawing = false
            this.$refs.canvas && (this.$refs.canvas.height = window.innerHeight)
        },

        close() {
            this.visible = false
            this.clear()
        },

        confirm() {
            this.onConfirm && this.onConfirm(this.getBase64())
            this.visible = false
        }
    },

    mounted() {
        window.addEventListener('resize', this.resize)

        this.$once('hook:beforeDestroy', () => {
            this.clear()
            window.removeEventListener('resize', this.resize)
        })
    }
}
</script>

<style lang="scss">
.signature-board {
    position: fixed;
    background-color: #ffffff;
    top: 0;
    right: 0;
    height: 100vh;
    width: 100vw;
    z-index: 100;
    overflow: hidden;

    &__footer {
        position: absolute;
        bottom: 20px;
        right: 20px;
    }
}
</style>
