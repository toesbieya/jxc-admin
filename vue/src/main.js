import Vue from 'vue'
import 'normalize.css/normalize.css' // a modern alternative to CSS resets
import Element from 'element-ui'
import ElementPersonal from '@ele'
import '@/asset/style/index.scss' // global css
import App from '@/App'
import store from '@/store'
import router from '@/router'
import '@/asset/icon'
import '@/util/errorLog'
import '@/directive'
import filters from './filter'
import globalMethod from '@/globalMethod'

Vue.use(Element)
Vue.use(ElementPersonal)
Vue.use(filters)
Vue.use(globalMethod)

Vue.config.productionTip = false

new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
})

//页面刷新时socket重连
store.dispatch('socket/init').catch()
