import Particle from "./Particle"

export default class ParticleNetwork {
    constructor(canvas) {
        this.canvas = canvas
        this.ctx = canvas.getContext("2d")
        this.options = {
            velocity: 1, // the higher the faster
            density: 15000, // the lower the denser
            netLineDistance: 200,
            netLineColor: '#929292',
            particleColors: ['#aaa'] // ['#6D4E5C', '#aaa', '#FFC458' ]
        }
        this.rAF = null
        this.timer = null
        this.stopSign = false
        this.loop = this.loop.bind(this)
        this.start()
    }

    start() {
        this.canvas.width = window.innerWidth
        this.canvas.height = window.innerHeight
        this.createParticles(true)
        this.bindUiActions()
        this.loop()
    }

    stop() {
        this.unbindUiActions()
        this.stopSign = true
        this.particles = []
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
    }

    loop() {
        if (this.stopSign) {
            this.rAF && window.cancelAnimationFrame(this.rAF)
            return
        }
        this.draw()
        this.rAF = window.requestAnimationFrame(this.loop)
    }

    draw() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
        this.ctx.globalAlpha = 1

        // Draw connections
        for (let i = 0; i < this.particles.length; i++) {
            for (let j = this.particles.length - 1; j > i; j--) {
                let distance, p1 = this.particles[i], p2 = this.particles[j]

                // check very simply if the two points are even a candidate for further measurements
                distance = Math.min(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y))
                if (distance > this.options.netLineDistance) continue

                // the two points seem close enough, now let's measure precisely
                distance = Math.sqrt(
                    Math.pow(p1.x - p2.x, 2) +
                    Math.pow(p1.y - p2.y, 2)
                )
                if (distance > this.options.netLineDistance) continue

                this.ctx.beginPath()
                this.ctx.strokeStyle = this.options.netLineColor
                this.ctx.globalAlpha = (this.options.netLineDistance - distance) / this.options.netLineDistance * p1.opacity * p2.opacity
                this.ctx.lineWidth = 0.7
                this.ctx.moveTo(p1.x, p1.y)
                this.ctx.lineTo(p2.x, p2.y)
                this.ctx.stroke()
            }
        }

        // Draw particles
        for (let i = 0; i < this.particles.length; i++) {
            this.particles[i].update()
            this.particles[i].draw()
        }
    }

    createParticles(isInitial) {
        // Initialise / reset particles
        this.particles = []
        let quantity = this.canvas.width * this.canvas.height / this.options.density

        if (isInitial) {
            let counter = 0
            window.clearInterval(this.timer)
            this.timer = window.setInterval(() => {
                if (counter < quantity - 1) this.particles.push(new Particle(this))
                else window.clearInterval(this.timer)
                counter++
            }, 250)
        }
        else for (let i = 0; i < quantity; i++) this.particles.push(new Particle(this))
    }

    createInteractionParticle() {
        this.interactionParticle = new Particle(this)
        this.interactionParticle.velocity = {x: 0, y: 0}
        this.particles.push(this.interactionParticle)
        return this.interactionParticle
    }

    removeInteractionParticle() {
        let index = this.particles.indexOf(this.interactionParticle)
        if (index > -1) {
            this.interactionParticle = undefined
            this.particles.splice(index, 1)
        }
    }

    bindUiActions() {
        this.spawnQuantity = 3
        this.mouseIsDown = false
        this.touchIsMoving = false

        this.onMouseMove = function (e) {
            !this.interactionParticle && this.createInteractionParticle()
            this.interactionParticle.x = e.offsetX
            this.interactionParticle.y = e.offsetY
        }.bind(this)

        this.onTouchMove = function (e) {
            e.preventDefault()
            this.touchIsMoving = true
            !this.interactionParticle && this.createInteractionParticle()
            this.interactionParticle.x = e.changedTouches[0].clientX
            this.interactionParticle.y = e.changedTouches[0].clientY
        }.bind(this)

        this.onMouseDown = function (e) {
            this.mouseIsDown = true
            let counter = 0
            let quantity = this.spawnQuantity
            let intervalId = window.setInterval(function () {
                if (this.mouseIsDown) {
                    if (counter === 1) quantity = 1
                    for (let i = 0; i < quantity; i++) {
                        if (this.interactionParticle) {
                            this.particles.push(new Particle(this, this.interactionParticle.x, this.interactionParticle.y))
                        }
                    }
                }
                else window.clearInterval(intervalId)
                counter++
            }.bind(this), 50)
        }.bind(this)

        this.onTouchStart = function (e) {
            e.preventDefault()
            window.setTimeout(function () {
                if (!this.touchIsMoving) {
                    for (let i = 0; i < this.spawnQuantity; i++) {
                        this.particles.push(new Particle(this, e.changedTouches[0].clientX, e.changedTouches[0].clientY))
                    }
                }
            }.bind(this), 200)
        }.bind(this)

        this.onMouseUp = function (e) {
            this.mouseIsDown = false
        }.bind(this)

        this.onMouseOut = function (e) {
            this.removeInteractionParticle()
        }.bind(this)

        this.onTouchEnd = function (e) {
            e.preventDefault()
            this.touchIsMoving = false
            this.removeInteractionParticle()
        }.bind(this)

        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.canvas)
        window.addEventListener('mousemove', this.onMouseMove)
        window.addEventListener('touchmove', this.onTouchMove)
        window.addEventListener('mousedown', this.onMouseDown)
        window.addEventListener('touchstart', this.onTouchStart)
        window.addEventListener('mouseup', this.onMouseUp)
        window.addEventListener('mouseout', this.onMouseOut)
        window.addEventListener('touchend', this.onTouchEnd)
    }

    unbindUiActions() {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect()
            this.resizeObserver = null
        }
        window.removeEventListener('mousemove', this.onMouseMove)
        window.removeEventListener('touchmove', this.onTouchMove)
        window.removeEventListener('mousedown', this.onMouseDown)
        window.removeEventListener('touchstart', this.onTouchStart)
        window.removeEventListener('mouseup', this.onMouseUp)
        window.removeEventListener('mouseout', this.onMouseOut)
        window.removeEventListener('touchend', this.onTouchEnd)
    }

    resize() {
        this.canvas.width = window.innerWidth
        this.canvas.height = window.innerHeight
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
        this.createParticles()
    }
}
