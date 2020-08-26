import Vue from 'vue'
import VIcon from '@/component/Icon'

Vue.component('v-icon', VIcon)

const req = require.context('./svg', false, /\.svg$/)
const keys = req.keys()
keys.map(req)
export default keys.map(i => i.match(/\.\/(.*)\.svg/)[1])
