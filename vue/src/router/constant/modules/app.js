/*路由表：系统页面*/
import Layout from '@/layout'
import Redirect from '@/views/app/redirect'
import Login from '@/views/app/login'
import Page404 from '@/views/app/404'
import Page403 from '@/views/app/403'

const router = [
    {
        path: '/redirect',
        component: Layout,
        children: [
            {
                path: '/redirect/:path(.*)',
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
