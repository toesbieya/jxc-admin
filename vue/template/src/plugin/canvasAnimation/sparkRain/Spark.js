import {rand} from "./util"

export default class Spark {
    constructor(x, y, options) {
        this.x = x
        this.y = y
        this.age = 0
        this.acceleration = rand(options.acceleration[0], options.acceleration[1])
        this.color = options.randColor ? rand(0, 255) + "," + rand(0, 255) + "," + rand(0, 255) : OPT.color
        this.opacity = options.maxOpacity - this.age / (options.lifetime * rand(1, 10))
    }

    go(options) {
        this.x += options.speed * options.direction.x * this.acceleration / 2
        this.y += options.speed * options.direction.y * this.acceleration / 2
        this.opacity = options.maxOpacity - ++this.age / options.lifetime
    }
}
