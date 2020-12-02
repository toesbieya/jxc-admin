import Particle from "./Particle"

export default class Sunlight {
    constructor(canvas) {
        this.canvas = canvas
        this.ctx = canvas.getContext('2d')
        this.parts = []
        this.mouse = {down: 0}
        this.rAF = null
        this.stopSign = false
        this.loop = this.loop.bind(this)
        this.mousedown = this.mousedown.bind(this)
        this.mouseup = this.mouseup.bind(this)
        this.start()
    }

    start() {
        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.canvas)
        window.addEventListener('mouseup', this.mouseup)
        window.addEventListener('mousedown', this.mousedown)
        this.resize()
        this.loop()
    }

    stop() {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect()
            this.resizeObserver = null
        }
        window.removeEventListener('mouseup', this.mouseup)
        window.removeEventListener('mousedown', this.mousedown)
        this.stopSign = true
        this.parts = []
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
    }

    loop() {
        if (this.stopSign) {
            this.rAF && window.cancelAnimationFrame(this.rAF)
            return
        }
        this.draw()
        this.rAF = window.requestAnimationFrame(this.loop)
    }

    draw() {
        this.step()
        this.ctx.globalCompositeOperation = 'destination-out'
        this.ctx.fillStyle = 'hsla(0, 0%, 0%, 0.6)'
        this.ctx.fillRect(0, 0, this.width, this.height)
        this.ctx.globalCompositeOperation = 'lighter'
        this.ctx.save()
        this.ctx.translate(this.width / 2, this.height / 2)
        this.ctx.rotate(this.tick / 300)
        let i = this.parts.length
        while (i--) {
            this.parts[i].draw(this)
        }
        this.ctx.restore()
    }

    step() {
        if (this.tick % 2 === 0 && this.parts.length < 200) {
            this.parts.push(new Particle())
        }
        let i = this.parts.length
        while (i--) {
            this.parts[i].step(this)
        }
        this.tick += this.mouse.down ? 3 : 1
    }

    mousedown() {
        this.mouse.down = 1
    }

    mouseup() {
        this.mouse.down = 0
    }

    resize() {
        this.tick = 0
        this.width = window.innerWidth
        this.height = window.innerHeight
        this.canvas.width = this.width
        this.canvas.height = this.height
        this.mouse.down = 0
        this.fl = 300
        this.bounds = {
            x: {min: -this.width / 2, max: this.width / 2},
            y: {min: -this.height / 2, max: this.height / 2},
            z: {min: -this.fl, max: 1000}
        }
        this.parts.length = 0
    }
}
