import Particle from './Particle'

export default class FluxRay {
    constructor(canvas) {
        this.canvas = canvas
        this.canvas.style.setProperty('background-color', 'black')
        this.ctx = canvas.getContext('2d')
        this.frame = 0
        this.damp = 0.00002 // remember a very small amount of the last direction
        this.accel = 100 // move very quickly
        this.attractors = Array.from({length: 8}, () => new Particle(canvas, this.accel))
        this.particles = Array.from({length: 1000}, () => new Particle(canvas, this.accel))
        this.rAF = null
        this.stopSign = false
        this.loop = this.loop.bind(this)
        this.start()
    }

    start() {
        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.canvas)
        this.resize()
        this.loop()
    }

    stop() {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect()
            this.resizeObserver = null
        }
        this.stopSign = true
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
        this.canvas.style.removeProperty('background-color')
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
        if (this.frame++ < 1000) {
            this.particles.forEach(p => p.step(this))
        }
    }

    reset() {
        this.ctx.globalCompositeOperation = "source-over"
        this.ctx.fillStyle = "#321"
        this.ctx.globalCompositeOperation = "lighter"
        this.particles.forEach(p => p.init(this.canvas, this.accel))
        this.attractors.forEach(a => a.init(this.canvas, this.accel))
        this.frame = 0
    }

    resize() {
        this.canvas.width = window.innerWidth
        this.canvas.height = window.innerHeight
        this.reset()
    }
}
