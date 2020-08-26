//路由表：系统页面

const router = [
    {
        path: 'index',
        component: 'index/',
        name: 'index',
        meta: {title: '首页', affix: true, icon: 'svg-home', noAuth: true, sort: 0}
    },
    {
        path: 'user',
        name: 'userCenter',
        component: 'userCenter/',
        meta: {title: '个人中心', noCache: true, icon: 'svg-user', noAuth: true, hidden: true},
    }
]

export default router
