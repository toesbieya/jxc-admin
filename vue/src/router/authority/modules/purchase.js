/*路由表：采购管理*/
import Layout from '@/layout'

const router = {
    path: '/purchase',
    component: Layout,
    alwaysShow: true,
    meta: {title: '采购管理', icon: 'shopping'},
    children: [
        {
            path: 'order',
            name: 'purchaseOrder',
            component: () => import('@/views/purchase/order'),
            meta: {title: '采购订单'}
        },
        {
            path: 'inbound',
            name: 'purchaseInbound',
            component: () => import('@/views/purchase/inbound'),
            meta: {title: '采购入库'}
        }
    ]
}

export default router
