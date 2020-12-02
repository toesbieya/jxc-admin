//路由表：默认页面

const router = [
    {
        path: 'index',
        component: 'admin/index/',
        name: 'index',
        meta: {title: '首页', affix: true, icon: 'svg-home', noAuth: true, pageHeader: false, sort: 0}
    }
]

export default router
