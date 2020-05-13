import RainDrop from "./RainDrop"

export default class Rain {
    constructor({rainDropCount, rainColor, backgroundColor}, canvas) {
        this.props = {rainDropCount, rainColor, backgroundColor}
        this.rainDrops = []
        this.timer = null
        this.rAF = null
        this.stopSign = false
        this.canvas = canvas
        this.ctx = canvas.getContext('2d')
        this.resize = this.resize.bind(this)
        this.start()
    }

    resize() {
        this.dimensions = {
            width: window.innerWidth,
            height: window.innerHeight
        }
        this.canvas.width = this.dimensions.width
        this.canvas.height = this.dimensions.height
        this.floor = this.dimensions.height * 0.7
    }

    draw() {
        this.ctx.fillStyle = this.props.backgroundColor
        this.ctx.fillRect(0, 0, this.dimensions.width, this.floor)
        for (let i = 0, len = this.rainDrops.length; i < len; i++) {
            let rainDrop = this.rainDrops[i]
            rainDrop.update()
            this.ctx.fillStyle = this.props.rainColor
            this.ctx.fillRect(rainDrop.position.x, rainDrop.position.y, rainDrop.size, rainDrop.size)
        }
        this.reflect()
    }

    reflect() {
        let grad = this.ctx.createLinearGradient(this.dimensions.width / 2, this.floor * 0.6, this.dimensions.width / 2, this.floor)
        grad.addColorStop(0, 'rgba(80,90,100,1)')
        grad.addColorStop(1, 'rgba(80,90,100,0)')
        this.ctx.save()
        this.ctx.scale(1, -1)
        this.ctx.translate(0, this.floor * -2)
        this.ctx.filter = 'blur(3px) saturate(150%)'
        this.ctx.drawImage(this.canvas, 0, 0, this.dimensions.width, this.floor, 0, 0, this.dimensions.width, this.floor)
        this.ctx.fillStyle = grad
        this.ctx.fillRect(0, 0, this.dimensions.width, this.floor)
        this.ctx.restore()
    }

    start() {
        window.addEventListener('resize', this.resize)
        this.resize()
        this.loop()
    }

    stop() {
        window.removeEventListener('resize', this.resize)
        this.stopSign = true
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
    }

    loop() {
        if (this.stopSign) {
            this.rAF && window.cancelAnimationFrame(this.rAF)
            this.timer && window.clearTimeout(this.timer)
            return
        }
        if (this.rainDrops.length < this.props.rainDropCount) {
            this.timer = window.setTimeout(() => this.rainDrops.push(new RainDrop(this)), Math.random() * 200)
        }
        else if (this.timer) {
            window.clearTimeout(this.timer)
            this.timer = null
        }
        this.draw()
        this.rAF = window.requestAnimationFrame(this.loop.bind(this))
    }
}
