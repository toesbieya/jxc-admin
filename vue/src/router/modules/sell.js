/*路由表：销售管理*/
import Layout from '@/layout'

const router = {
    path: '/sell',
    component: Layout,
    alwaysShow: true,
    meta: {title: '销售管理', icon: 'sell', breadcrumb: false},
    children: [
        {
            path: 'order',
            name: 'sellOrder',
            component: () => import('@/views/sell/order'),
            meta: {title: '销售订单'}
        },
        {
            path: 'outbound',
            name: 'sellOutbound',
            component: () => import('@/views/sell/outbound'),
            meta: {title: '销售出库'}
        }
    ]
}

export default router
