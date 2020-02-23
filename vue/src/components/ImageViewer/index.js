import Vue from 'vue'
import Main from './main.vue'

const ImageViewerConstructor = Vue.extend(Main)

let instance

const image = function ({index, urlList}) {
    if (instance) instance.close()
    else {
        instance = new ImageViewerConstructor({data: {index, urlList}})
        instance.$mount()
        document.body.appendChild(instance.$el)
    }
    instance.urlList = urlList || []
    instance.index = index
    instance.value = true
    return instance
}

image.close = function () {
    if (instance) {
        instance.close()
        instance = null
    }
}

export default image
