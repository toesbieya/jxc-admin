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

//获取项目部署的绝对路径
export function getContextPath() {
    const {protocol, host} = document.location
    return `${protocol}//${host}${process.env.BASE_URL}`
}
