/*
 * Live2D Widget
 * https://github.com/stevenjoezhang/live2d-widget
 *
 * API 后端可自行搭建，参考 https://github.com/fghrsh/live2d_api
 */

export default class Waifu {
    constructor(tipsPath, apiPath) {
        this.tipsPath = tipsPath
        this.apiPath = apiPath

        this.setUserAction = this.setUserAction.bind(this)
        this.visibilityChange = this.visibilityChange.bind(this)

        this.userAction = false
        this.userActionTimer = null
        this.messageTimer = null
        this.messageArray = [
            "好久不见，日子过得好快呢……",
            "大坏蛋！你都多久没理人家了呀，嘤嘤嘤～",
            "嗨～快来逗我玩吧！",
            "拿小拳拳锤你胸口！",
            "记得把小家加入 Adblock 白名单哦！"
        ]

        sessionStorage.removeItem("waifu-text")

        this.insertWaifu()

        this.addEventListener()

        this.welcomeMessage()

        this.detectUserAction()

        this.initModel()

        this.extra()
    }

    insertWaifu() {
        document.body.insertAdjacentHTML("beforeend",
            `
            <div id="waifu">
                <div id="waifu-tips"></div>
			    <canvas id="live2d" width="300" height="300"></canvas>
			    <div id="waifu-tool">
				    <span class="el-icon-s-promotion" title="打飞机"></span>
				    <span class="el-icon-refresh" title="换人"></span>
				    <span class="el-icon-s-operation" title="换装"></span>
				    <span class="el-icon-camera" title="拍照"></span>
				    <span class="el-icon-switch-button" title="关闭"></span>
			    </div>
		    </div>
            `
        )

        window.setTimeout(() => document.getElementById("waifu").style.bottom = '0', 0)
    }

    addEventListener() {
        document.querySelector("#waifu-tool .el-icon-s-promotion").addEventListener("click", this.startAsteroids)
        document.querySelector("#waifu-tool .el-icon-refresh").addEventListener("click", this.loadOtherModel.bind(this))
        document.querySelector("#waifu-tool .el-icon-s-operation").addEventListener("click", this.loadRandModel.bind(this))
        document.querySelector("#waifu-tool .el-icon-camera").addEventListener("click", this.takePicture.bind(this))
        document.querySelector("#waifu-tool .el-icon-switch-button").addEventListener("click", this.exit.bind(this))

        window.addEventListener("mousemove", this.setUserAction)
        window.addEventListener("keydown", this.setUserAction)
        window.addEventListener("visibilitychange", this.visibilityChange)
    }

    removeEventListener() {
        window.removeEventListener("mousemove", this.setUserAction)
        window.removeEventListener("keydown", this.setUserAction)
        window.removeEventListener("visibilitychange", this.visibilityChange)
    }

    detectUserAction() {
        window.setInterval(() => {
            if (this.userAction) {
                this.userAction = false
                window.clearInterval(this.userActionTimer)
                this.userActionTimer = null
            }
            else if (!this.userActionTimer) {
                this.userActionTimer = window.setInterval(() => {
                    this.showMessage(this.randomSelection(this.messageArray), 6000, 9)
                }, 20000)
            }
        }, 1000)
    }

    setUserAction() {
        this.userAction = true
    }

    startAsteroids() {
        if (window.Asteroids) {
            if (!window.ASTEROIDSPLAYERS) window.ASTEROIDSPLAYERS = []
            window.ASTEROIDSPLAYERS.push(new window.Asteroids())
        }
        else {
            const script = document.createElement("script")
            script.src = "https://cdn.jsdelivr.net/gh/GalaxyMimi/CDN/asteroids.js"
            document.head.appendChild(script)
        }
    }

    takePicture() {
        this.showMessage("照好了嘛，是不是很可爱呢？", 6000, 9)
        window.Live2D.captureName = "photo.png"
        window.Live2D.captureFrame = true
    }

    welcomeMessage() {
        let text, now = new Date().getHours()

        if (now > 5 && now <= 7) text = "早上好！一日之计在于晨，美好的一天就要开始了。"
        else if (now > 7 && now <= 11) text = "上午好！工作顺利嘛，不要久坐，多起来走动走动哦！"
        else if (now > 11 && now <= 13) text = "中午了，工作了一个上午，现在是午餐时间！"
        else if (now > 13 && now <= 17) text = "午后很容易犯困呢，今天的运动目标完成了吗？"
        else if (now > 17 && now <= 19) text = "傍晚了！窗外夕阳的景色很美丽呢，最美不过夕阳红～"
        else if (now > 19 && now <= 21) text = "晚上好，今天过得怎么样？"
        else if (now > 21 && now <= 23) text = ["已经这么晚了呀，早点休息吧，晚安～", "深夜时要爱护眼睛呀！"]
        else text = "你是夜猫子呀？这么晚还不睡觉，明天起的来嘛？"

        this.showMessage(text, 7000, 8)
    }

    visibilityChange() {
        !document.hidden && this.showMessage("哇，你终于回来了～", 6000, 9)
    }

    randomSelection(array) {
        return Array.isArray(array) ? array[Math.floor(Math.random() * array.length)] : array
    }

    initModel() {
        let modelId = localStorage.getItem("modelId"),
            modelTexturesId = localStorage.getItem("modelTexturesId")

        if (modelId == null) {
            // 首次访问加载 指定模型 的 指定材质
            modelId = 1 // 模型 ID
            modelTexturesId = 53 // 材质 ID
        }

        this.loadModel(modelId, modelTexturesId)

        fetch(this.tipsPath)
            .then(response => response.json())
            .then(result => {
                for (const key of ['mouseover', 'click']) {
                    window.addEventListener(key, event => {
                        for (const tips of result[key]) {
                            if (!event.target.matches(tips.selector)) continue

                            const text = this.randomSelection(tips.text).replace("{text}", event.target.innerText)

                            return this.showMessage(text, 4000, 8)
                        }
                    })
                }
            })
    }

    loadModel(modelId, modelTexturesId) {
        localStorage.setItem("modelId", modelId)

        if (modelTexturesId === undefined) modelTexturesId = 0

        localStorage.setItem("modelTexturesId", modelTexturesId)

        window.loadlive2d(
            "live2d",
            `${this.apiPath}/get/?id=${modelId}-${modelTexturesId}`,
            console.log(`Live2D 模型 ${modelId}-${modelTexturesId} 加载完成`)
        )
    }

    //调用一言api
    showHitokoto() {
        // 增加 hitokoto.cn 的 API
        fetch("https://v1.hitokoto.cn")
            .then(response => response.json())
            .then(result => {
                const text = `这句一言来自 <span>「${result.from}」</span>，是 <span>${result.creator}</span> 在 hitokoto.cn 投稿的。`

                this.showMessage(result.hitokoto, 6000, 9)

                window.setTimeout(() => this.showMessage(text, 4000, 9), 6000)
            })
    }

    showMessage(text, timeout, priority) {
        if (!text) return

        if (!sessionStorage.getItem("waifu-text") ||
            sessionStorage.getItem("waifu-text") <= priority) {
            if (this.messageTimer) {
                window.clearTimeout(this.messageTimer)
                this.messageTimer = null
            }

            if (Array.isArray(text)) text = this.randomSelection(text)

            sessionStorage.setItem("waifu-text", priority)

            const tips = document.getElementById("waifu-tips")
            if (!tips) return

            tips.innerHTML = text
            tips.classList.add("waifu-tips-active")

            this.messageTimer = window.setTimeout(() => {
                sessionStorage.removeItem("waifu-text")
                tips.classList.remove("waifu-tips-active")
            }, timeout)
        }
    }

    //换装
    loadRandModel() {
        const modelId = localStorage.getItem("modelId"),
            modelTexturesId = localStorage.getItem("modelTexturesId")

        // 可选 "rand"(随机), "switch"(顺序)
        fetch(`${this.apiPath}/rand_textures/?id=${modelId}-${modelTexturesId}`)
            .then(response => response.json())
            .then(result => {
                if (result.textures.id === 1 &&
                    (modelTexturesId === '1' || modelTexturesId === '0')) {
                    this.showMessage("我还没有其他衣服呢！", 4000, 10)
                }
                else this.showMessage("我的新衣服好看嘛？", 4000, 10)

                this.loadModel(modelId, result.textures.id)
            })
    }

    //切换其他模型
    loadOtherModel() {
        const modelId = localStorage.getItem("modelId")

        fetch(`${this.apiPath}/switch/?id=${modelId}`)
            .then(response => response.json())
            .then(result => {
                this.loadModel(result.model.id)
                this.showMessage(result.model.message, 4000, 10)
            })
    }

    extra() {
        const devtools = () => ({})
        console.log("%c", devtools)
        devtools.toString = () => this.showMessage("哈哈，你打开了控制台，是想要看看我的小秘密吗？", 6000, 9)

        console.log(`
  く__,.ヘヽ.        /  ,ー､ 〉
           ＼ ', !-─‐-i  /  /´
           ／｀ｰ'       L/／｀ヽ､
         /   ／,   /|   ,   ,       ',
       ｲ   / /-‐/  ｉ  L_ ﾊ ヽ!   i
        ﾚ ﾍ 7ｲ｀ﾄ   ﾚ'ｧ-ﾄ､!ハ|   |
          !,/7 '0'     ´0iソ|    |
          |.从"    _     ,,,, / |./    |
          ﾚ'| i＞.､,,__  _,.イ /   .i   |
            ﾚ'| | / k_７_/ﾚ'ヽ,  ﾊ.  |
              | |/i 〈|/   i  ,.ﾍ |  i  |
             .|/ /  ｉ：    ﾍ!    ＼  |
              kヽ>､ﾊ    _,.ﾍ､    /､!
              !'〈//｀Ｔ´', ＼ ｀'7'ｰr'
              ﾚ'ヽL__|___i,___,ンﾚ|ノ
                  ﾄ-,/  |___./
                  'ｰ'    !_,.:
`)
    }

    exit() {
        this.showMessage("愿你有一天能与重要的人重逢。", 2000, 11)

        this.removeEventListener()

        const dom = document.getElementById("waifu")
        dom.style.bottom = "-500px"

        window.setTimeout(() => dom.remove && dom.remove(), 3000)
    }
}
