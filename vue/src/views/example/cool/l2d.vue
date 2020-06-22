<template>
    <div>
        <div class="tip-row">
            <a href="https://github.com/stevenjoezhang/live2d-widget" target="_blank">
                l2d看板娘
            </a>
        </div>
    </div>
</template>

<script>
    import {getContextPath} from "@/utils/browser"

    /**
     * 加载js或css
     * @param url
     * @param type js|css
     * @returns {Promise<String>} url
     */
    function loadExternalResource(url, type = 'js') {
        return new Promise((resolve, reject) => {
            let tag

            if (type === "css") {
                const links = Array.from(document.getElementsByTagName('link'))
                if (links.some(link => link.getAttribute('href') === url)) return resolve()

                tag = document.createElement("link")
                tag.rel = "stylesheet"
                tag.href = url
            }
            else if (type === "js") {
                const scripts = Array.from(document.getElementsByTagName('script'))
                if (scripts.some(script => script.getAttribute('src') === url)) return resolve()

                tag = document.createElement("script")
                tag.src = url
            }

            if (tag) {
                tag.onload = () => resolve(url)
                tag.onerror = () => reject(url)
                document.head.appendChild(tag)
            }
            else reject(`没有这个东东,url:${url},type:${type}`)
        })
    }

    export default {
        name: "l2d",
        mounted() {
            //注意：live2d_path 参数应使用绝对路径
            //const live2d_path = "https://cdn.jsdelivr.net/gh/stevenjoezhang/live2d-widget/"
            const live2d_path = `${getContextPath()}static/live2d/`

            // 加载 waifu.css live2d.min.js waifu-tips.js
            Promise.all([
                loadExternalResource(live2d_path + "waifu.css", "css"),
                loadExternalResource(live2d_path + "live2d.min.js"),
                loadExternalResource(live2d_path + "waifu-tips.js")
            ])
                .then(() => {
                    // initWidget 第一个参数为 waifu-tips.json 的路径，第二个参数为 API 地址
                    // API 后端可自行搭建，参考 https://github.com/fghrsh/live2d_api
                    // 初始化看板娘会自动加载指定目录下的 waifu-tips.json
                    window.initWidget(live2d_path + "waifu-tips.json", "https://live2d.fghrsh.net/api")
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
                })
        }
    }
</script>
