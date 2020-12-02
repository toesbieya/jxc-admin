import Vector from "./Vector"
import Trail from "./Trail"

export default class Rocket extends Trail {
    constructor(childFactory, explosionFactory, position, velocity = new Vector()) {
        super(childFactory, position, velocity)

        this.explosionFactory = explosionFactory
        this.lifetime = 10
    }

    update(time) {
        if (this.getRemainingLifetime() && this.velocity.y > 0) {
            this.explosionFactory(this)
            this.lifetime = 0
        }

        super.update(time)
    }
}
