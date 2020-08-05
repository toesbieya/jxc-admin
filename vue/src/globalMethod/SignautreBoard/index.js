import Vue from 'vue'
import Main from './main.vue'

const Constructor = Vue.extend(Main)

let instance

const signature = function ({image, lineWidth = 6, lineColor = '#000000', onConfirm}) {
    if (instance) instance.close()
    else {
        instance = new Constructor().$mount()
        document.body.appendChild(instance.$el)
    }

    instance.image = image
    instance.lineWidth = lineWidth
    instance.lineColor = lineColor
    instance.onConfirm = onConfirm
    instance.init()
    instance.visible = true

    return instance
}

signature.close = function () {
    if (instance) {
        instance.close()
        instance = null
    }
}

export default signature
