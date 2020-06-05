/*路由表：系统页面*/
import Layout from '@/layout'

const router = [
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
        alias: '/register',
        component: () => import('@/views/app/login')
    },
    {
        path: '/404',
        component: () => import('@/views/app/404')
    },
    {
        path: '/403',
        component: () => import('@/views/app/403')
    }
]

export default router
