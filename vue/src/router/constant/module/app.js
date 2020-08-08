//路由表：系统页面
import Layout from '@/layout'
import Redirect from '@/view/app/redirect'
import Login from '@/view/app/login'
import Page404 from '@/view/app/404'
import Page403 from '@/view/app/403'
import {lazyLoadView} from "@/router/util"

const router = [
    {
        path: '/',
        component: Layout,
        redirect: '/index',
        children: [
            {
                path: 'index',
                component: lazyLoadView(import('@/view/index')),
                name: 'index',
                meta: {title: '首页', affix: true, icon: 'home', sort: 0}
            },
            {
                path: 'user',
                name: 'userCenter',
                component: lazyLoadView(import('@/view/userCenter')),
                meta: {title: '个人中心', noCache: true, icon: 'user', hidden: true},
            }
        ]
    },
    {
        path: 'https://doc.toesbieya.cn',
        meta: {title: '文档', icon: 'documentation', sort: 1}
    },
    {
        path: '/redirect',
        component: Layout,
        children: [
            {
                path: ':path(.*)',
                props: true,
                component: Redirect
            }
        ]
    },
    {
        path: '/login',
        alias: '/register',
        component: Login
    },
    {
        path: '/404',
        component: Page404
    },
    {
        path: '/403',
        component: Page403
    }
]

export default router
