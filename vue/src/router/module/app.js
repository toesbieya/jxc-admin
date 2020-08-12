//路由表：系统页面

const router = [
    {
        path: '/',
        component: 'Layout',
        children: [
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
    },
    {
        path: 'https://doc.toesbieya.cn',
        meta: {title: '文档', icon: 'svg-documentation', noAuth: true, sort: 1}
    }
]

export default router
