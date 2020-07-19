/*路由表：采购管理*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/document/purchase',
    component: Layout,
    alwaysShow: true,
    meta: {title: '采购管理', icon: 'shopping'},
    children: [
        {
            path: 'order',
            name: 'purchaseOrder',
            component: () => lazyLoadView(import('@/views/purchase/order')),
            meta: {title: '采购订单'}
        },
        {
            path: 'order/detail/:type(see|add|edit)/:id?',
            name: 'purchaseOrderDetail',
            hidden: true,
            props: true,
            component: () => lazyLoadView(import('@/views/purchase/order/detail')),
            meta: {
                title: '采购订单详情',
                activeMenu: '/document/purchase/order',
                noCache: true,
                isDetailPage: true
            }
        },
        {
            path: 'inbound',
            name: 'purchaseInbound',
            component: () => lazyLoadView(import('@/views/purchase/inbound')),
            meta: {title: '采购入库'}
        },
        {
            path: 'inbound/detail/:type(see|add|edit)/:id?',
            name: 'purchaseInboundDetail',
            hidden: true,
            props: true,
            component: () => lazyLoadView(import('@/views/purchase/inbound/detail')),
            meta: {
                title: '采购入库详情',
                activeMenu: '/document/purchase/inbound',
                noCache: true,
                isDetailPage: true
            }
        }
    ]
}

export default router
