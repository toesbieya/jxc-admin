/*路由表：系统页面*/
import Layout from '@/layout'
import Redirect from '@/view/app/redirect'
import Login from '@/view/app/login'
import Page404 from '@/view/app/404'
import Page403 from '@/view/app/403'

const router = [
    {
        path: '/redirect',
        component: Layout,
        children: [
            {
                path: ':path(.*)',
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
