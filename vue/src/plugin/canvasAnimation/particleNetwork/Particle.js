function getLimitedRandom(min, max, roundToInteger) {
    let number = Math.random() * (max - min) + min
    if (roundToInteger) number = Math.round(number)
    return number
}

function returnRandomArrayItem(array) {
    return array[Math.floor(Math.random() * array.length)]
}

export default class Particle {
    constructor(parent, x, y) {
        this.network = parent
        this.canvas = parent.canvas
        this.ctx = parent.ctx
        this.particleColor = returnRandomArrayItem(this.network.options.particleColors)
        this.radius = getLimitedRandom(1.5, 2.5)
        this.opacity = 0
        this.x = x || Math.random() * this.canvas.width
        this.y = y || Math.random() * this.canvas.height
        this.velocity = {
            x: (Math.random() - 0.5) * parent.options.velocity,
            y: (Math.random() - 0.5) * parent.options.velocity
        }
    }

    update() {
        this.opacity = this.opacity < 1 ? this.opacity + 0.01 : 1

        // Change dir if outside map
        if (this.x > this.canvas.width + 100 || this.x < -100) {
            this.velocity.x = -this.velocity.x
        }
        if (this.y > this.canvas.height + 100 || this.y < -100) {
            this.velocity.y = -this.velocity.y
        }

        // Update position
        this.x += this.velocity.x
        this.y += this.velocity.y
    }

    draw() {
        this.ctx.beginPath()
        this.ctx.fillStyle = this.particleColor
        this.ctx.globalAlpha = this.opacity
        this.ctx.arc(this.x, this.y, this.radius, 0, 2 * Math.PI)
        this.ctx.fill()
    }
}
