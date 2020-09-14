//根据body宽度判断是否为移动端，是则返回true
export function isMobile() {
    const rect = document.body.getBoundingClientRect()
    return rect.width < 501
}

export function isDom(obj) {
    return obj && typeof obj === 'object' && obj.nodeType === 1 && typeof obj.nodeName === 'string'
}

export function getElementInnerWidth(ele) {
    if (!ele) return 0

    const style = window.getComputedStyle(ele)

    return parseFloat(style.width) - (parseFloat(style.paddingLeft) + parseFloat(style.paddingRight))
}

export function getElementHeight(ele, style) {
    if (!ele) return 0

    const node = ele.cloneNode(true)
    node.style.opacity = '0'

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

//获取元素距离其容器的顶部距离
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
 * @desc 加载js或css
 * @param url
 * @param type js|css
 * @returns {Promise<String>} 加载成功返回url（若资源此前已加载，返回undefined）
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

/**
 * @desc 平滑滚动至指定的位置
 * @param el 滚动容器，或可用于querySelector的字符串，或一个返回DOM的函数
 * @param position 滚动的目的地
 * @param options
 */
export function scrollTo(el, position, options) {
    const {
        callback,          //滚动完成的回调
        duration = 300,    //滚动耗时
        direction = 'top'  //滚动方向，top滚动至距元素顶部distance的位置，left滚动至距元素左边distance的位置
    } = options || {}

    if (typeof el === 'string') el = document.querySelector(el)
    else if (typeof el === 'function') el = el()

    if (!isDom(el)) return

    const elPosition = getScroll(el, direction === 'top')
    const scrollFunc = ((el, direction) => {
        if (el === window) {
            return direction === 'top'
                ? y => el.scrollTo(window.pageXOffset, window.pageYOffset + y)
                : x => el.scrollTo(window.pageXOffset + x, window.pageYOffset)
        }
        return direction === 'top'
            ? y => el.scrollTop += y
            : x => el.scrollLeft += x
    })(el, direction)

    let times = duration / 16, distance = (position - elPosition) / (times + 1)

    const frameFunc = () => {
        if (times > 0) {
            scrollFunc(distance)
            times--
            return window.requestAnimationFrame(frameFunc)
        }
        typeof callback === 'function' && callback()
    }

    return window.requestAnimationFrame(frameFunc)
}

export function getScroll(el, top) {
    return el === window
        ? el[top ? 'pageYOffset' : 'pageXOffset']
        : el[top ? 'scrollTop' : 'scrollLeft']
}
