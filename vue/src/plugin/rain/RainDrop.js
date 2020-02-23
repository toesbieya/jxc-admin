import Vector from "./Vector"

export default class RainDrop {
    constructor(parent) {
        this.size = 2
        this.parent = parent
        this.init()
    }

    init() {
        this.life = 0
        this.ttl = Math.random() * 500 + 300
        this.position = new Vector(Math.random() * window.innerWidth, 0)
        this.velocity = new Vector(0.5 - Math.random(), Math.random() + 0.2)
    }

    update() {
        if (this.position.x > window.innerWidth || this.position.x < -this.size || this.life > this.ttl) {
            this.init()
        }
        if (this.position.y > this.parent.floor) {
            this.position.y = this.parent.floor - this.size
            this.velocity.y *= -0.2 - Math.random() * 0.3
        }
        this.life++
        this.position.add(this.velocity)
        this.velocity.y += 0.1
    }
}
