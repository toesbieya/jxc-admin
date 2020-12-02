export default class Vector {
    constructor(x, y) {
        this.x = x || 0
        this.y = y || 0
    }

    add({x, y}) {
        this.x += x
        this.y += y
    }
}
