import Spark from "./Spark"
import {rand} from "./util"

export default class SparkRain {
    constructor(canvas) {
        this.canvas = canvas
        this.ctx = canvas.getContext("2d")
        this.options = {
            amount: 10000,
            speed: 0.1, // pixels per frame
            lifetime: 500,
            direction: {x: -0.5, y: 1},
            size: [1, 1],
            maxOpacity: 1,
            color: "150, 150, 150",
            randColor: true,
            acceleration: [5, 40]
        }
        if (window.innerWidth < 520) {
            this.options.speed = 0.05
            this.options.color = "150, 150, 150"
        }
        this.sparks = []
        this.rAF = null
        this.timer = null
        this.stopSign = false
        this.loop = this.loop.bind(this)
        this.addSpark = this.addSpark.bind(this)
        this.start()
    }

    start() {
        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.canvas)
        this.resize()
        this.timer = window.setInterval(
            () => this.sparks.length < this.options.amount && this.addSpark(),
            1000 / this.options.amount)
        this.loop()
    }

    stop() {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect()
            this.resizeObserver = null
        }
        this.stopSign = true
        this.sparks = []
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
        this.ctx.fillStyle = 'rgba(0,0,0, 0.1)'
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)
        for (let i = 0; i < this.sparks.length; i++) {
            this.sparks[i].opacity <= 0 ? this.sparks.splice(i, 1) : this.drawSpark(this.sparks[i])
        }
    }

    addSpark() {
        let x = rand(-200, this.canvas.width + 200)
        let y = rand(-200, this.canvas.height + 200)
        this.sparks.push(new Spark(x, y, this.options))
    }

    drawSpark(spark) {
        spark.go(this.options)
        this.ctx.beginPath()
        this.ctx.fillStyle = `rgba(${spark.color},${spark.opacity})`
        this.ctx.rect(spark.x, spark.y, this.options.size[0], this.options.size[1])
        this.ctx.fill()
    }

    resize() {
        this.canvas.width = window.innerWidth
        this.canvas.height = window.innerHeight
    }
}

