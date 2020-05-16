import Vue from 'vue'
import 'normalize.css/normalize.css' // a modern alternative to CSS resets
import Element from 'element-ui'
import '@/assets/styles/index.scss' // global css
import App from '@/App'
import store from '@/store'
import router from '@/router'
import '@/assets/icons'
import '@/utils/errorLog'
import '@/directive'
import * as filters from './filter'
import BottomTip from '@/components/BottomTip'
import Guide from '@/components/Guide'
import ImageViewer from '@/components/ImageViewer'
import Message from '@/components/Message'

Vue.use(Element)

//注册过滤器
Object.keys(filters).forEach(key => {
    Vue.filter(key, filters[key])
})

//刷新时socket重连
store.dispatch('socket/init', store.state.user).then()

//注册插件
Vue.prototype.$bottomTip = BottomTip
Vue.prototype.$guide = Guide
Vue.prototype.$image = ImageViewer
Vue.prototype.$message = Message

Vue.config.productionTip = false

new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
})
