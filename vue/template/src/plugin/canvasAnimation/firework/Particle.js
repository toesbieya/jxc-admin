import Vector from "./Vector"
import Time from "./Time"

export default class Particle {
    constructor(position, velocity = new Vector(), color = 'white', radius = 1, lifetime = 1, mass = 1) {
        this.position = position
        this.velocity = velocity
        this.color = color
        this.radius = radius
        this.lifetime = lifetime
        this.mass = mass

        this.isInCanvas = true
        this.createdOn = Time.now()
    }

    update(time) {
        if (!this.getRemainingLifetime()) return

        this.velocity.add(Particle.GRAVITATION.clone().multiplyScalar(this.mass))
        this.position.add(this.velocity.clone().multiplyScalar(time.delta))
    }

    render(canvas, context) {
        const remainingLifetime = this.getRemainingLifetime()

        if (!remainingLifetime) return

        const radius = this.radius * remainingLifetime

        context.globalAlpha = remainingLifetime
        context.globalCompositeOperation = 'lighter'
        context.fillStyle = this.color

        context.beginPath()
        context.arc(this.position.x, this.position.y, radius, 0, Math.PI * 2)
        context.fill()
    }

    getRemainingLifetime() {
        const elapsedLifetime = Time.now() - this.createdOn
        return Math.max(0, this.lifetime - elapsedLifetime) / this.lifetime
    }
}
