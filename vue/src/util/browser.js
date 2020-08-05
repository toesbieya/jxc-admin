const {body} = document
const WIDTH = 768

export function isMobile() {
    const rect = body.getBoundingClientRect()
    return rect.width - 1 < WIDTH
}

//获取元素的内宽度
export function getElementInnerWidth(ele) {
    if (!ele) return 0

    const style = window.getComputedStyle(ele)
    return parseFloat(style.width) - (parseFloat(style.paddingLeft) + parseFloat(style.paddingRight))
}

//获取元素的高度
export function getElementHeight(ele, style) {
    if (!ele) return 0

    let node = ele.cloneNode(true)
    node.style.visibility = 'hidden'

    if (style) {
        Object.keys(style).forEach(item => {
            node.style[item] = style[item]
        })
    }

    const id = 'temp-id-' + Date.now()
    node.setAttribute('id', id)
    document.body.append(node)

    const height = node.offsetHeight
    document.body.removeChild(node)

    return height
}

export function getOffsetTop(element, container) {
    if (!element) return 0

    if (!element.getClientRects().length) {
        return 0
    }

    const rect = element.getBoundingClientRect()

    if (rect.width || rect.height) {
        if (container === window) {
            container = element.ownerDocument.documentElement
            return rect.top - container.clientTop
        }
        return rect.top - container.getBoundingClientRect().top
    }

    return rect.top
}

/**
 * 加载js或css
 *
 * @param url
 * @param type js|css
 * @returns {Promise<String>} url
 */
export function loadExternalResource(url, type = 'js') {
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

export function scrollTo(y, options = {}) {
    const raf = window.requestAnimationFrame
    const {getContainer = () => window, callback, duration = 450} = options

    const container = getContainer()
    const scrollTop = getScroll(container, true)
    const startTime = Date.now()

    const frameFunc = () => {
        const timestamp = Date.now()
        const time = timestamp - startTime
        const nextScrollTop = easeInOutCubic(time > duration ? duration : time, scrollTop, y, duration)
        if (container === window) {
            container.scrollTo(window.pageXOffset, nextScrollTop)
        }
        else {
            container.scrollTop = nextScrollTop
        }
        if (time < duration) {
            raf(frameFunc)
        }
        else if (typeof callback === 'function') {
            callback()
        }
    }
    raf(frameFunc)
}

export function getScroll(target, top) {
    const prop = top ? 'pageYOffset' : 'pageXOffset'
    const method = top ? 'scrollTop' : 'scrollLeft'
    return target === window ? target[prop] : target[method]
}

export function easeInOutCubic(t, b, c, d) {
    const cc = c - b
    t /= d / 2
    if (t < 1) {
        return (cc / 2) * t * t * t + b
    }
    return (cc / 2) * ((t -= 2) * t * t + 2) + b
}
