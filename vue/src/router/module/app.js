//路由表：系统页面
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = [
    {
        path: '/',
        component: Layout,
        redirect: '/index',
        children: [
            {
                path: 'index',
                component: lazyLoadView('index/'),
                name: 'index',
                meta: {title: '首页', affix: true, icon: 'home', noAuth: true, sort: 0}
            },
            {
                path: 'user',
                name: 'userCenter',
                component: lazyLoadView('userCenter/'),
                meta: {title: '个人中心', noCache: true, icon: 'user', noAuth: true, hidden: true},
            }
        ]
    },
    {
        path: 'https://doc.toesbieya.cn',
        meta: {title: '文档', icon: 'documentation', noAuth: true, sort: 1}
    }
]

export default router
