class Pointer {
    constructor() {
        this.id = -1
        this.texcoordX = 0
        this.texcoordY = 0
        this.prevTexcoordX = 0
        this.prevTexcoordY = 0
        this.deltaX = 0
        this.deltaY = 0
        this.down = false
        this.moved = false
        this.color = [30, 0, 300]
    }
}

export default Pointer
