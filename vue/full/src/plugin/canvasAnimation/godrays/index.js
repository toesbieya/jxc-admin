function mountainHeight(position, roughness) {
    const frequencies = [1721, 947, 547, 233, 73, 31, 7]
    return frequencies.reduce((height, freq) => height * roughness - Math.cos(freq * position), 0)
}

export default class Godrays {
    constructor(canvas) {
        this.canvas = canvas
        this.ctx = canvas.getContext('2d')
        this.frame = 0
        this.godraysCanvas = canvas.cloneNode()
        this.godraysCtx = this.godraysCanvas.getContext('2d')
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
        this.godraysCtx.clearRect(0, 0, this.godraysCanvas.width, this.godraysCanvas.height)
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
        this.canvas.width = 512
        this.canvas.height = 256
        this.godraysCanvas.width = 128
        this.godraysCanvas.height = 64
        let sunY = Math.cos(this.frame++ / 512) * 24
        let emissionGradient = this.godraysCtx.createRadialGradient(
            this.godraysCanvas.width / 2, this.godraysCanvas.height / 2 + sunY, // The sun's center.
            0,                                                        // Start radius.
            this.godraysCanvas.width / 2, this.godraysCanvas.height / 2 + sunY, // Sun's center again.
            44                                                        // End radius.
        )
        this.godraysCtx.fillStyle = emissionGradient
        emissionGradient.addColorStop(.1, '#0C0804') // Color for pixels in radius 0 to 4.4 (44 * .1).
        emissionGradient.addColorStop(.2, '#060201') // Color for everything past radius 8.8.
        this.godraysCtx.fillRect(0, 0, this.godraysCanvas.width, this.godraysCanvas.height)
        this.godraysCtx.fillStyle = '#000'
        let skyGradient = this.ctx.createLinearGradient(0, 0, 0, this.canvas.height)
        skyGradient.addColorStop(0, '#2a3e55') // Blueish at the top.
        skyGradient.addColorStop(.7, '#8d4835') // Reddish at the bottom.
        this.ctx.fillStyle = skyGradient
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)

        for (let i = 0; i < 4; i++) {
            this.ctx.fillStyle = `hsl(7, 23%, ${23 - i * 6}%)`
            for (let x = this.canvas.width; x--;) {
                let mountainPosition = (this.frame + this.frame * i * i) / 3000 + x / 2000
                let mountainRoughness = i / 19 - .5
                let y = 128 + i * 25 + mountainHeight(mountainPosition, mountainRoughness) * 45
                this.ctx.fillRect(x, y, 1, 999)
                this.godraysCtx.fillRect(x / 4, y / 4 + 1, 1, 999)
            }
        }
        this.ctx.globalCompositeOperation = this.godraysCtx.globalCompositeOperation = 'lighter'
        for (let scaleFactor = 1.07; scaleFactor < 5; scaleFactor *= scaleFactor) {
            this.godraysCtx.drawImage(
                this.godraysCanvas,
                (this.godraysCanvas.width - this.godraysCanvas.width * scaleFactor) / 2,
                (this.godraysCanvas.height - this.godraysCanvas.height * scaleFactor) / 2 - sunY * scaleFactor + sunY,
                this.godraysCanvas.width * scaleFactor,
                this.godraysCanvas.height * scaleFactor
            )
        }
        this.ctx.drawImage(this.godraysCanvas, 0, 0, this.canvas.width, this.canvas.height)
    }

    resize() {
        this.canvas.width = window.innerWidth
        this.canvas.height = window.innerHeight
    }
}
