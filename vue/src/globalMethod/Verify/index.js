import Vue from 'vue'
import Main from './main.vue'
import {isEmpty} from "@/util"

let Constructor = Vue.extend(Main)

export default function ({left, top} = {}) {
    const instance = new Constructor()
    const position = getPosition({height: instance.height + 130, width: instance.width + 20, left, top})

    instance.positionTop = position.top
    instance.positionLeft = position.left
    instance.$mount()
    document.body.appendChild(instance.$el)
    instance.visible = true

    return new Promise((resolve, reject) => {
        instance.resolve = resolve
        instance.reject = reject
    })
}

function getPosition({height, width, left, top} = {}) {
    const windowHeight = window.innerHeight
    const windowWidth = window.innerWidth

    //若任一位置信息为空，直接返回居中位置
    if (isEmpty(left, top)) {
        return {
            top: center(windowHeight, height),
            left: center(windowWidth, width)
        }
    }

    //判断位置是否超出屏幕外
    if (windowWidth < left + width) left = center(windowWidth, width)
    if (windowHeight < top + height) top = center(windowHeight, height)

    return {left, top}
}

function center(full, part) {
    return Math.round((full - part) / 2)
}
