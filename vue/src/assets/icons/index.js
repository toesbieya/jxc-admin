import Vue from 'vue'
import SvgIcon from '@/components/SvgIcon'

Vue.component('svg-icon', SvgIcon)

const req = require.context('./svg', false, /\.svg$/)
const keys = req.keys()
keys.map(req)
export default keys.map(i => i.match(/\.\/(.*)\.svg/)[1])
