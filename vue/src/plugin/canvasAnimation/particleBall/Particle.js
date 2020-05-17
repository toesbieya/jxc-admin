import Vector from "./Vector"
import util from "./util"

export default class Particle {
    constructor({add, sphereRad, fLen, dotImageList}) {
        this.distPos = {
            theta: add === true ? Math.PI / 2 : Math.random() * Math.PI,
            phi: 2 * Math.random() * Math.PI
        }

        this.distPos.x = sphereRad * Math.sin(this.distPos.theta) * Math.cos(this.distPos.phi)
        this.distPos.y = sphereRad * Math.sin(this.distPos.theta) * Math.sin(this.distPos.phi)
        this.distPos.z = sphereRad * Math.cos(this.distPos.theta)
        this.distVec = new Vector(this.distPos.x, this.distPos.y, this.distPos.z)
        this.unitVec = this.distVec.unit()

        this.startVec = this.distVec.multiply(1 + Math.random() * 2)
        this.x = this.startVec.x
        this.y = this.startVec.y
        this.z = this.startVec.z

        this.veloRate = 1 + Math.random()
        this.velo = this.unitVec.negative().multiply(this.veloRate)
        this.finalVelo = 0
        this.m = fLen / (fLen - this.z)

        this.age = this.life = 50 + Math.floor(Math.random() * 500)
        this.turnAngle = 0
        this.wanderTime = 200
        this.radius = 1 + Math.random() * 3

        let colorRandom = Math.floor(Math.random() * 4)
        switch (colorRandom) {
            case 0:
                this.img = dotImageList[0]
                this.color = "rgba(70,255,140,"
                break
            case 1:
                this.img = dotImageList[1]
                this.color = "rgba(90,90,90,"
                break
            case 2:
                this.img = dotImageList[2]
                break
            case 3:
                this.img = dotImageList[3]
                break
        }
    }

    update({sphereRad, fLen, turnSpeed}) {
        this.nowPos = new Vector(this.x, this.y, this.z)
        this.nowPosUnit = this.nowPos.unit()

        if (this.wanderTime > 0 && this.nowPos.length() > (sphereRad * 1.2)) {
            this.wanderTime--
            this.velo.x += 0.1 * (Math.random() * 2 - 1)
            this.velo.y += 0.1 * (Math.random() * 2 - 1)
            this.velo.z += 0.1 * (Math.random() * 2 - 1)
            this.x = this.x + this.velo.x
            this.y = this.y + this.velo.y
            this.z = this.z + this.velo.z
            this.op = util.map(this.nowPos.length(), sphereRad, this.startVec.length(), 1, 0)
        }
        else if (this.nowPos.length() > sphereRad) {
            if (this.finalPos === 0) {
                this.finalPos = this.nowPosUnit.multiply(sphereRad)
            }
            if (this.finalVelo === 0) {
                this.finalVelo = this.nowPosUnit.negative().multiply(this.veloRate)
            }
            this.x = this.x + this.finalVelo.x
            this.y = this.y + this.finalVelo.y
            this.z = this.z + this.finalVelo.z
            this.op = util.map(this.nowPos.length(), sphereRad, this.startVec.length(), 1, 0)
        }
        else {
            this.op = this.life / (this.age / 2)
            this.turnAngle = (this.turnAngle + turnSpeed) % (Math.PI * 2)
            let cosAngle = Math.cos(turnSpeed)
            let sinAngle = Math.sin(turnSpeed)
            this.x = cosAngle * this.nowPos.x + sinAngle * this.nowPos.z
            this.z = -sinAngle * this.nowPos.x + cosAngle * this.nowPos.z
            this.y = this.nowPos.y
            this.life--
        }

        this.m = fLen / (fLen - this.z)
    }
}
