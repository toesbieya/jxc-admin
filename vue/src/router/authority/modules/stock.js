/*路由表：库存管理*/
import Layout from '@/layout'

const router = {
    path: '/stock',
    component: Layout,
    alwaysShow: true,
    meta: {title: '库存管理', icon: 'stock'},
    children: [
        {
            path: 'current',
            name: 'currentStock',
            component: () => import('@/views/stock/current'),
            meta: {title: '当前库存'}
        }
    ]
}

export default router
