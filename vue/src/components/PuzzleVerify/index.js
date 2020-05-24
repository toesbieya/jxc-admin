import Vue from 'vue'
import Main from './main.vue'
import {PopupManager} from 'element-ui/lib/utils/popup'

let Constructor = Vue.extend(Main)

const PuzzleVerify = function ({left = 0, top = 0} = {}) {
    const instance = new Constructor({data: {positionLeft: left}})
    instance.positionTop = top - Math.round((instance.height + 130) / 2)
    instance.$mount()
    document.body.appendChild(instance.$el)
    instance.$el.style.zIndex = PopupManager.nextZIndex()
    instance.visible = true
    return new Promise((resolve, reject) => {
        instance.resolve = resolve
        instance.reject = reject
    })
}

export default PuzzleVerify
