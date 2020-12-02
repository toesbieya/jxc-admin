import Vue from 'vue'
import Main from './main.vue'
import {mergeObj} from "@/util"

const GuideConstructor = Vue.extend(Main)

let instance

const defaultOptions = {
    stageBackground: '#ffffff',
    doneBtnText: '完成',
    closeBtnText: '关闭',
    nextBtnText: '下一步',
    prevBtnText: '上一步',
}

const guide = function (steps, index, beforeExit) {
    if (instance) instance.exit()
    else {
        instance = new GuideConstructor({data: {options: defaultOptions}})
        instance.$mount()
        document.body.appendChild(instance.$el)
    }

    instance.steps = steps
    instance.beforeExit = beforeExit

    this.$nextTick(() => instance.start(index))
}

guide.config = function (config) {
    mergeObj(defaultOptions, config)
}

guide.exit = function (force) {
    instance && instance.exit(force)
}

export default guide
