/*顶级菜单：后台管理*/

import {context2array} from '@/router/util'

const files = require.context('./child', false, /\.js$/)

export default {
    path: '/',
    component: 'Layout',
    meta: {title: '后台管理', icon: 'svg-home', sort: 0},
    children: context2array(files)
}
