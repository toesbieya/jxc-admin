function colorIntToHexString(color) {
    let s = color.toString(16)
    return '0'.repeat(6 - s.length) + s
}

export default class ParticleWave {
    constructor(canvas) {
        this.canvas = canvas
        this.ctx = canvas.getContext("2d")
        this.config = {
            colors: {
                background: 0x000000,
                particle: 0x477cc2
            },
            alpha: {
                particle: 1
            },
            particleCount: 10000
        }
        this.particleWaveWalker = 0
        this.rAF = null
        this.stopSign = false
        this.loop = this.loop.bind(this)
        this.start()
    }

    initParticle() {
        this.particles = new Float32Array(this.config.particleCount * 2)
        for (let i = 0; i < this.particles.length; i += 2) {
            this.particles[i] = Math.random()
            this.particles[i + 1] = Math.random()
        }
    }

    initParticleColor() {
        this.particleColorRGB = new Float32Array(3)
        this.particleColorRGB[0] = this.config.colors.particle >> 16 & 0xff
        this.particleColorRGB[1] = this.config.colors.particle >> 8 & 0xff
        this.particleColorRGB[2] = this.config.colors.particle & 0xff
        this.particleFillStyle = 'rgb(' + this.particleColorRGB[0] + ',' + this.particleColorRGB[1] + ',' + this.particleColorRGB[2] + ')'
    }

    initSmoothGradient() {
        this.smoothGradient = this.ctx.createLinearGradient(
            this.canvas.width / 2,
            0,
            this.canvas.width / 2,
            this.canvas.height
        )
        this.smoothGradient.addColorStop(0.25, 'rgba(0, 0, 0, 0)')
        this.smoothGradient.addColorStop(0.45, 'rgba(0, 0, 0, 0.9)')
        this.smoothGradient.addColorStop(0.5, 'rgba(0, 0, 0, 1)')
        this.smoothGradient.addColorStop(0.55, 'rgba(0, 0, 0, 0.9)')
        this.smoothGradient.addColorStop(0.75, 'rgba(0, 0, 0, 0)')
    }

    initWaterGradient() {
        this.waterGradient = this.ctx.createLinearGradient(
            this.canvas.width / 2,
            this.canvas.height / 2,
            this.canvas.width / 2,
            this.canvas.height
        )
        this.waterGradient.addColorStop(0, 'rgba(0, 0, 30, 0)')
        this.waterGradient.addColorStop(1, 'rgba(30, 0, 60, 0.5)')
    }

    start() {
        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.canvas)
        this.resize()
        this.initParticle()
        this.initParticleColor()
        this.initSmoothGradient()
        this.initWaterGradient()
        this.loop()
    }

    stop() {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect()
            this.resizeObserver = null
        }
        this.stopSign = true
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
        this.ctx.fillStyle = '#' + colorIntToHexString(this.config.colors.background)
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)
        this.ctx.fillStyle = this.waterGradient
        this.ctx.fillRect(0, this.canvas.height / 2, this.canvas.width, this.canvas.height / 2)

        this.renderParticle()

        this.ctx.fillStyle = this.particleFillStyle
        this.ctx.fillStyle = this.smoothGradient
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)
    }

    renderParticle() {
        this.particleWaveWalker += 0.03
        this.ctx.fillStyle = this.particleFillStyle

        let radius = {min: 1, add: 5},
            midY = this.canvas.height / 2,
            midX = this.canvas.width / 2,
            spreadX = 5,
            spreadZ = 0.0,
            modZ = 0.0,
            addX = 0,
            addY = 0,
            p = {x: 0.0, y: 0.0, r: 0.0},
            waveControl = 10

        for (let i = 0, xIndex, zIndex; i < this.particles.length; i += 2) {
            xIndex = i
            zIndex = i + 1
            this.particles[zIndex] += 0.003
            if (this.particles[zIndex] > 1) {
                this.particles[zIndex] = 0
                this.particles[xIndex] = Math.random()
            }

            if (this.particles[zIndex] < 0.3) continue

            modZ = Math.pow(this.particles[zIndex], 2)
            spreadZ = 1 + (spreadX - 1) * modZ
            addX = (0.5 - this.particles[xIndex]) * this.canvas.width * spreadZ
            addY = midY * modZ * (1 + 3 / waveControl)

            p.x = midX + addX
            p.y = midY + addY
            p.r = radius.min + modZ * radius.add
            p.y += Math.sin(this.particles[xIndex] * 50 + this.particleWaveWalker) * addY / waveControl
            p.y += Math.cos(this.particles[zIndex] * 10 + this.particleWaveWalker) * addY / waveControl
            p.y -= Math.cos(this.particles[zIndex] + this.particles[xIndex] * 10 + this.particleWaveWalker) * addY / waveControl
            p.y -= Math.cos(this.particles[xIndex] * 50 + this.particleWaveWalker) * addY / waveControl
            p.y -= Math.sin(this.particles[zIndex] * 10 + this.particleWaveWalker) * addY / waveControl

            if (p.x < 0 || p.x > this.canvas.width) continue

            this.ctx.fillRect(p.x, p.y, p.r, p.r)
        }
    }

    resize() {
        this.canvas.width = window.innerWidth
        this.canvas.height = window.innerHeight
    }
}
