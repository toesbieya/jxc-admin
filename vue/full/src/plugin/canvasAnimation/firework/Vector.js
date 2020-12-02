export default class Vector {
    constructor(x = 0, y = 0) {
        this.x = x
        this.y = y
    }

    add(v) {
        this.x += v.x
        this.y += v.y
        return this
    }

    multiplyScalar(s) {
        this.x *= s
        this.y *= s
        return this
    }

    clone() {
        return new Vector(this.x, this.y)
    }
}
