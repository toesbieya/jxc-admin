import Vue from 'vue'
import 'normalize.css/normalize.css' // a modern alternative to CSS resets
import Element from 'element-ui'
import ElementPersonal from '@ele'
import '@/assets/styles/index.scss' // global css
import App from '@/App'
import store from '@/store'
import router from '@/router'
import '@/assets/icons'
import '@/utils/errorLog'
import '@/directive'
import filters from './filter'
import globalMethods from '@/globalMethods'

Vue.use(Element)
Vue.use(ElementPersonal)
Vue.use(filters)
Vue.use(globalMethods)

Vue.config.productionTip = false

new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
})

//页面刷新时socket重连
store.dispatch('socket/init', store.state.user).catch()

window.addEventListener('unhandledrejection', event => {
    if (event.reason.stack.startsWith('Error: Redirected when going from')) {
        event.preventDefault()
    }
})
