import Vue from 'vue'
import Main from './main.vue'
import {PopupManager} from 'element-ui/lib/utils/popup'
import {isVNode} from 'element-ui/lib/utils/vdom'

let MessageConstructor = Vue.extend(Main)

let instance
let instances = []
let seed = 1

//键：options.type+'|'+options.message，值：message实例
let groups = {}

const Message = function (options) {
    options = options || {}
    if (typeof options === 'string') {
        options = {message: options}
    }
    if (!options.type) {
        options.type = 'info'
    }
    const group = options.type + '|' + options.message
    const originalMessage = groups[group]
    //若不是新的message
    if (originalMessage) {
        //旧实例的badge+1，并重新计时（如果有的话）
        originalMessage.badge++
        originalMessage.clearTimer(true)
        originalMessage.startTimer()
        return originalMessage
    }

    let userOnClose = options.onClose
    let id = 'message_' + seed++

    options.onClose = function () {
        Message.close(id, userOnClose)
    }
    instance = new MessageConstructor({data: options})
    instance.id = id
    instance.group = group
    if (isVNode(instance.message)) {
        instance.$slots.default = [instance.message]
        instance.message = null
    }
    instance.$mount()
    document.body.appendChild(instance.$el)
    let verticalOffset = options.offset || 20
    instances.forEach(item => {
        verticalOffset += item.$el.offsetHeight + 16
    })
    instance.verticalOffset = verticalOffset
    instance.visible = true
    instance.$el.style.zIndex = PopupManager.nextZIndex()
    instances.push(instance)
    groups[group] = instance
    return instance
};

['success', 'warning', 'info', 'error'].forEach(type => {
    Message[type] = options => {
        if (typeof options === 'string') {
            options = {message: options}
        }
        options.type = type
        return Message(options)
    }
})

Message.close = function (id, userOnClose) {
    let len = instances.length
    let index = -1
    let removedHeight
    for (let i = 0; i < len; i++) {
        if (id === instances[i].id) {
            removedHeight = instances[i].$el.offsetHeight
            index = i
            if (typeof userOnClose === 'function') {
                userOnClose(instances[i])
            }
            delete groups[instances[i].group]
            instances.splice(i, 1)
            break
        }
    }
    if (len <= 1 || index === -1 || index > instances.length - 1) return
    for (let i = index; i < len - 1; i++) {
        let dom = instances[i].$el
        dom.style.top =
            parseInt(dom.style.top, 10) - removedHeight - 16 + 'px'
    }
}

Message.closeAll = function () {
    for (let i = instances.length - 1; i >= 0; i--) {
        instances[i].close()
    }
}

export default Message
