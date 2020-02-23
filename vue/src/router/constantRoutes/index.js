/*
* 不需要权限控制的路由表
* */
import Layout from '@/layout'
import exampleRouter from '@/router/modules/example'

const constantRoutes = [
    {
        path: '/redirect',
        component: Layout,
        children: [
            {
                path: '/redirect/:path(.*)',
                component: () => import('@/views/app/redirect')
            }
        ]
    },
    {
        path: '/login',
        component: () => import('@/views/app/login')
    },
    {
        path: '/register',
        component: () => import('@/views/app/register')
    },
    {
        path: '/404',
        component: () => import('@/views/app/404')
    },
    {
        path: '/403',
        component: () => import('@/views/app/403')
    },
    {
        path: '/',
        component: Layout,
        redirect: '/index',
        children: [
            {
                path: 'index',
                component: () => import('@/views/index'),
                name: 'index',
                meta: {title: '首页', affix: true, icon: 'home'}
            }
        ]
    },
    {
        path: '/user',
        component: Layout,
        redirect: '/user/index',
        children: [
            {
                path: 'index',
                name: 'userCenter',
                component: () => import('@/views/userCenter'),
                meta: {title: '个人中心', noCache: true, icon: 'user'},
            }
        ],
        hidden: true
    },
    exampleRouter
]

function addNoAuth(routes) {
    routes.forEach(route => {
        if (!route.meta) route.meta = {}
        route.meta.noAuth = true
        if (Array.isArray(route.children)) addNoAuth(route.children)
    })
}

addNoAuth(constantRoutes)

export default constantRoutes
