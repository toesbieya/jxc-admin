/*顶级菜单：开发用的页面*/
import {context2array} from '@/router/util'

const files = require.context('./child', false, /\.js$/)

export default {
    path: '/dev',
    component: {template: `<div><div>开发专用页面</div><router-view/></div>`},
    meta: {title: '开发专用页面', icon: 'svg-develop', noAuth: true, noCache: true, alwaysShow: true},
    children: context2array(files)
}
