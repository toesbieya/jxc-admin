import Particle from "./Particle"

export default class MoveFollowMouse {
    constructor(canvas, {num = 10, gravityDistance = 100} = {}) {
        this.canvas = canvas
        this.ctx = canvas.getContext('2d')
        this.rAF = null
        this.stopSign = false
        this.particles = []
        this.num = num
        this.gravityDistance = gravityDistance
        this.mousePos = {x: 0, y: 0}
        this.loop = this.loop.bind(this)
        this.getMousePos = this.getMousePos.bind(this)
        this.start()
    }

    start() {
        for (let i = 0; i < this.num; i += 1) {
            this.particles.push(new Particle())
        }
        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.canvas)
        window.addEventListener('mousemove', this.getMousePos)
        this.resize()
        this.loop()
    }

    stop() {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect()
            this.resizeObserver = null
        }
        window.removeEventListener('mousemove', this.getMousePos)
        this.stopSign = true
        this.particles = []
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
        this.ctx.clearRect(0, 0, this.width, this.height)
        for (let i = 0; i < this.num; i += 1) {
            this.particles[i].update({
                ctx: this.ctx,
                width: this.width,
                height: this.height,
                gravityDistance: this.gravityDistance,
                mousePos: this.mousePos
            })
        }
    }

    getMousePos(evt) {
        const rect = this.canvas.getBoundingClientRect()
        this.mousePos = {x: evt.clientX - rect.left, y: evt.clientY - rect.top}
    }

    resize() {
        this.width = this.canvas.width = window.innerWidth
        this.height = this.canvas.height = window.innerHeight
    }
}
