/*顶级菜单：开发用的页面*/
import component from "@/view/dev"

export default {
    path: '/dev',
    component,
    meta: {title: '开发专用页面', icon: 'svg-develop', noAuth: true, noCache: true}
}
