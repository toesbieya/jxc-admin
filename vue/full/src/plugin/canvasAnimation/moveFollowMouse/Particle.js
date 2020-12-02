export default class Particle {
    constructor() {
        this.angleX = Math.random() * Math.PI * 2
        this.angleY = Math.random() * Math.PI * 2
        this.speedX = Math.random()
        this.speedY = Math.random()
        this.radius = 500
    }

    update({ctx, width, height, gravityDistance, mousePos}, speed = 0.035) {
        let x = Math.cos(this.angleX) * this.radius,
            y = Math.sin(this.angleY) * this.radius,
            currentSpeedX = this.speedX * speed,
            currentSpeedY = this.speedY * speed

        if (Math.abs(mousePos.x - width / 2 - x) < gravityDistance && Math.abs(mousePos.y - height / 2 - y) < gravityDistance) {
            currentSpeedX = currentSpeedX * (Math.abs(mousePos.x - width / 2 - x) / gravityDistance) * 0.5
            currentSpeedY = currentSpeedY * (Math.abs(mousePos.y - height / 2 - y) / gravityDistance) * 0.5
        }

        this.angleX += currentSpeedX
        this.angleY += currentSpeedY

        ctx.beginPath()
        ctx.fillStyle = "white"

        ctx.arc(width / 2 + x, height / 2 + y, 2.5, 0, Math.PI * 2, false)
        ctx.fill()
    }
}
