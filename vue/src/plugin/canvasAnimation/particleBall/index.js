import Particle from "./Particle"
import {getContextPath} from "@/utils/browser"

export default class ParticleBall {
    constructor(canvas, {sphereRad = 130, fLen = 300, maxParticle = 400, turnSpeed = 0.005} = {}) {
        this.canvas = canvas
        this.ctx = canvas.getContext("2d")
        this.rAF = null
        this.stopSign = false
        this.sphereRad = sphereRad
        this.fLen = fLen
        this.maxParticle = maxParticle
        this.turnSpeed = turnSpeed
        this.particles = []
        this.resize = this.resize.bind(this)
        this.loop = this.loop.bind(this)
        this.start()
    }

    start() {
        window.addEventListener('resize', this.resize)
        this.resize()
        this.initDot()
        this.loop()
    }

    stop() {
        window.removeEventListener('resize', this.resize)
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
        if (this.particles.length < this.maxParticle) {
            this.particles.push(new Particle({
                sphereRad: this.sphereRad,
                fLen: this.fLen,
                dotImageList: this.dotImageList
            }))
        }
        for (let i = 0; i < this.particles.length; i++) {
            let p = this.particles[i]
            if (p.life === 0) {
                this.particles.splice(i, 1)
                this.particles.push(new Particle({
                    sphereRad: this.sphereRad,
                    fLen: this.fLen,
                    dotImageList: this.dotImageList
                }))
            }

            p.update({sphereRad: this.sphereRad, fLen: this.fLen, turnSpeed: this.turnSpeed})

            if (p.m > 0) {
                this.ctx.save()
                this.ctx.globalAlpha = p.op
                this.ctx.drawImage(p.img, p.x * p.m + this.CenterX, this.CenterY - p.y * p.m, p.radius * p.m * 2, p.radius * p.m * 2)
                this.ctx.restore()
            }
        }
    }

    resize() {
        this.width = this.canvas.width = window.innerWidth
        this.height = this.canvas.height = window.innerHeight
        this.CenterX = this.width / 2
        this.CenterY = this.height / 2
    }

    initDot() {
        this.dotImageList = []
        for (let i = 1; i <= 4; i++) {
            const dotImage = new Image()
            dotImage.src = `${getContextPath()}static/img/dot${i}.png`
            this.dotImageList.push(dotImage)
        }
    }
}
