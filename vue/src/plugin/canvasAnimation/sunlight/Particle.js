function rand(min, max) {
    return Math.random() * (max - min) + min
}

export default class Particle {
    constructor() {
        this.reset()
    }

    reset() {
        this.x = 0
        this.y = 0
        this.z = 0
        this.vx = rand(-0.5, 0.5)
        this.vy = rand(-0.5, 0.5)
        this.vz = rand(-0.25, 0.5)
        this.s = 0
        this.sx = 0
        this.sy = 0
        this.life = 1
        this.decay = rand(0.005, 0.02)
        this.radius = rand(5, 15)
        this.sradius = this.radius
        this.rradius = this.radius
        this.hue = 0
        this.alpha = 1
        this.angle = 0
    }

    step($) {
        this.vx *= $.mouse.down ? 1.1 : 1.04
        this.vy *= $.mouse.down ? 1.1 : 1.04
        this.vz *= $.mouse.down ? 1.1 : 1.04
        this.x += this.vx
        this.y += this.vy
        this.z += this.vz
        this.s = $.fl / ($.fl + this.z)
        this.sx = this.x * this.s
        this.sy = this.y * this.s
        this.sradius = this.radius * this.s
        this.rradius = Math.max(0.0001, this.sradius * this.life)
        this.angle = Math.atan2(this.sy, this.sx)
        this.hue = (this.angle / (Math.PI * 2)) * 180 + $.tick * 4
        this.alpha = this.life
        if (this.z < $.bounds.z.min) return this.reset()
        if (this.life > 0) this.life -= this.decay
        else this.reset()
    }

    draw($) {
        $.ctx.beginPath()
        $.ctx.arc(this.sx, this.sy, this.rradius * 2, 0, Math.PI * 2)
        $.ctx.fillStyle = 'hsla(' + (this.hue + 60) + ', 60%, 30%, ' + this.alpha / 3 + ')'
        $.ctx.fill()
        $.ctx.strokeStyle = 'hsla(' + (this.hue - 60) + ', 60%, 30%, ' + this.alpha / 2 + ')'
        $.ctx.stroke()

        let angle1 = this.angle + Math.PI / 2,
            angle2 = this.angle,
            angle3 = this.angle - Math.PI / 2

        $.ctx.beginPath()
        $.ctx.moveTo(0, 0)
        $.ctx.lineTo(this.sx + Math.cos(angle1) * this.rradius, this.sy + Math.sin(angle1) * this.rradius)
        $.ctx.lineTo(this.sx + Math.cos(angle2) * this.rradius * 6, this.sy + Math.sin(angle2) * this.rradius * 6)
        $.ctx.lineTo(this.sx + Math.cos(angle3) * this.rradius, this.sy + Math.sin(angle3) * this.rradius)
        $.ctx.closePath()
        $.ctx.fillStyle = 'hsla(' + this.hue + ', 50%, 30%, ' + this.alpha / 2 + ')'
        $.ctx.fill()

        $.ctx.beginPath()
        $.ctx.moveTo(this.sx + Math.cos(angle2) * this.rradius * 6, this.sy + Math.sin(angle2) * this.rradius * 6)
        $.ctx.lineTo(0, 0)
        $.ctx.strokeStyle = 'hsla(' + this.hue + ', 50%, 30%, ' + this.alpha + ')'
        $.ctx.stroke()

        let sparkleRadius = this.rradius * 4
        $.ctx.fillStyle = 'hsla(' + (this.hue + 180) + ', 100%, 50%, ' + this.alpha * 2 + ')'
        $.ctx.fillRect((this.sx + rand(-sparkleRadius, sparkleRadius)), (this.sy + rand(-sparkleRadius, sparkleRadius)), 1, 1)
    }
}
