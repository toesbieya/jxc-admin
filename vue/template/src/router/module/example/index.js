/*顶级菜单：演示用例*/

import {context2array} from '@/router/util'

const files = require.context('./child', false, /\.js$/)

export default {
    path: '/example',
    component: 'Layout',
    meta: {title: '演示用例', icon: 'svg-show', noAuth: true, noCache: true, sort: 1},
    children: context2array(files)
}
