import Vue from 'vue'
import Main from './main.vue'
import {mergeObj} from "@/utils"

const GuideConstructor = Vue.extend(Main)

let instance

const defaultOptions = {
    stageBackground: '#ffffff',
    doneBtnText: '完成',
    closeBtnText: '关闭',
    nextBtnText: '下一步',
    prevBtnText: '上一步',
}

const guide = function (start, steps, beforeExist) {
    if (instance) instance.exit()
    else {
        instance = new GuideConstructor({data: {options: defaultOptions}})
        instance.$mount()
        document.body.appendChild(instance.$el)
    }

    instance.steps = steps
    instance.beforeExist = beforeExist

    this.$nextTick(() => instance.start(start))
}

guide.config = function (config) {
    mergeObj(defaultOptions, config)
}

guide.exit = function () {
    instance && instance.exit()
}

export default guide
