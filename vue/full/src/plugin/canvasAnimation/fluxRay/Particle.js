export default class Particle {
    constructor(canvas, accel) {
        this.init(canvas, accel)
    }

    init(canvas, accel) {
        this.x = Math.random() * canvas.width
        this.y = Math.random() * canvas.height
        this.vx = accel * (Math.random() - Math.random())
        this.vy = accel * (Math.random() - Math.random())
    }

    step(parent) {
        // move towards every attractor
        // at a speed inversely proportional to distance squared
        // (much slower when further away, very fast when close)
        for (const a of parent.attractors) {
            // calculate the square of the distance
            // from this particle to the current attractor
            const dx = a.x - this.x
            const dy = a.y - this.y
            const d2 = dx * dx + dy * dy
            if (d2 > 0.1) {
                // make sure we don't divide by zero
                // accelerate towards each attractor
                this.vx += parent.accel * dx / d2
                this.vy += parent.accel * dy / d2
            }
        }
        // move by the velocity
        this.x += this.vx
        this.y += this.vy
        // scale the velocity back for the next frame
        this.vx *= parent.damp
        this.vy *= parent.damp
        // draw particle
        parent.ctx.fillRect(this.x, this.y, 0.5, 0.5)
    }
}
