import Vue from 'vue'
import Main from './main.vue'

const BottomTipConstructor = Vue.extend(Main)

let instance

const bottomTip = function (message) {
    instance && instance.close()
    instance = new BottomTipConstructor({data: {message}})
    instance.$mount()
    document.body.appendChild(instance.$el)
    instance.value = true
    return instance
}

bottomTip.close = function () {
    if (instance) {
        instance.close()
        instance = null
    }
}

export default bottomTip
