//路由表：默认页面

/**
 * @type {import('vue-router').RouteConfig[]}
 */
const router = [
    {
        path: 'index',
        component: 'admin/index/',
        name: 'index',
        meta: {title: '首页', affix: true, icon: 'svg-home', noAuth: true, pageHeader: false, sort: 0}
    },
    {
        path: 'user',
        name: 'userCenter',
        component: 'admin/userCenter/',
        meta: {title: '个人中心', noCache: true, icon: 'svg-user', noAuth: true, pageHeader: false, hidden: true},
    }
]

export default router
