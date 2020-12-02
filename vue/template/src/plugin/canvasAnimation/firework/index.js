import Vector from "./Vector"
import Time from "./Time"
import Particle from "./Particle"
import Trail from "./Trail"
import Rocket from "./Rocket"

Particle.GRAVITATION = new Vector(0, 9.81)

const getTrustParticleFactory = function () {
    function getColor() {
        const hue = Math.floor(Math.random() * 15 + 30)
        return `hsl(${hue}, 100%, 75%`
    }

    return function () {
        const position = this.position.clone()
        const velocity = this.velocity.clone().multiplyScalar(-.1)
        velocity.x += (Math.random() - .5) * 8
        const color = getColor()
        const radius = 1 + Math.random()
        const lifetime = .5 + Math.random() * .5
        const mass = .01

        return new Particle(position, velocity, color, radius, lifetime, mass)
    }
}

const getExplosionFactory = function (baseHue) {
    function getColor() {
        const hue = Math.floor(baseHue + Math.random() * 15) % 360
        const lightness = Math.floor(Math.pow(Math.random(), 2) * 50 + 50)
        return `hsl(${hue}, 100%, ${lightness}%`
    }

    function getChildFactory() {
        return function (parent) {
            const direction = Math.random() * Math.PI * 2
            const force = 8
            const velocity = new Vector(Math.cos(direction) * force, Math.sin(direction) * force)
            const color = getColor()
            const radius = 1 + Math.random()
            const lifetime = 1
            const mass = .1

            return new Particle(parent.position.clone(), velocity, color, radius, lifetime, mass)
        }
    }

    function getTrail(position) {
        const direction = Math.random() * Math.PI * 2
        const force = Math.random() * 128
        const velocity = new Vector(Math.cos(direction) * force, Math.sin(direction) * force)
        const lifetime = .5 + Math.random()
        const mass = .075

        return new Trail(getChildFactory(), position, velocity, lifetime, mass)
    }

    return function (parent) {
        let trails = 32
        while (trails--) {
            parent.children.push(getTrail(parent.position.clone()))
        }
    }
}

export default class Firework {
    constructor(canvas) {
        this.canvas = canvas
        this.ctx = canvas.getContext('2d')
        this.time = new Time()
        this.rockets = []
        this.rAF = null
        this.timer = null
        this.stopSign = false
        this.loop = this.loop.bind(this)
        this.addRocket = this.addRocket.bind(this)
        this.start()
    }

    start() {
        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.canvas)
        window.addEventListener('click', this.addRocket)
        this.timer = window.setInterval(this.addRocket, 1000)
        this.resize()
        this.loop()
    }

    stop() {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect()
            this.resizeObserver = null
        }
        window.removeEventListener('click', this.addRocket)
        this.stopSign = true
        this.rockets = []
        this.time = undefined
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
    }

    loop() {
        if (this.stopSign) {
            this.rAF && window.cancelAnimationFrame(this.rAF)
            this.timer && window.clearInterval(this.timer)
            return
        }
        this.draw()
        this.rAF = window.requestAnimationFrame(this.loop)
    }

    draw() {
        this.time.update()
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)

        for (let i = 0; i < this.rockets.length; i++) {
            this.rockets[i].update(this.time)
            this.rockets[i].render(this.canvas, this.ctx)
        }
    }

    addRocket() {
        const trustParticleFactory = getTrustParticleFactory()
        const explosionFactory = getExplosionFactory(Math.random() * 360)

        const position = new Vector(Math.random() * this.canvas.width, this.canvas.height)
        const thrust = window.innerHeight * .75
        const angle = Math.PI / -2 + (Math.random() - .5) * Math.PI / 8
        const velocity = new Vector(Math.cos(angle) * thrust, Math.sin(angle) * thrust)
        const lifetime = 3

        this.rockets.push(new Rocket(trustParticleFactory, explosionFactory, position, velocity, lifetime))

        this.rockets = this.rockets.filter(rocket => rocket.isAlive)
    }

    resize() {
        this.canvas.width = window.innerWidth
        this.canvas.height = window.innerHeight
    }
}
