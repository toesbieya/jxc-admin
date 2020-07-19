/*路由表：销售管理*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/document/sell',
    component: Layout,
    alwaysShow: true,
    meta: {title: '销售管理', icon: 'sell'},
    children: [
        {
            path: 'order',
            name: 'sellOrder',
            component: () => lazyLoadView(import('@/views/sell/order')),
            meta: {title: '销售订单'}
        },
        {
            path: 'order/detail/:type(see|add|edit)/:id?',
            name: 'purchaseOrderDetail',
            hidden: true,
            props: true,
            component: () => lazyLoadView(import('@/views/sell/order/detail')),
            meta: {
                title: '销售订单详情',
                activeMenu: '/document/sell/order',
                noCache: true,
                isDetailPage: true
            }
        },
        {
            path: 'outbound',
            name: 'sellOutbound',
            component: () => lazyLoadView(import('@/views/sell/outbound')),
            meta: {title: '销售出库'}
        },
        {
            path: 'outbound/detail/:type(see|add|edit)/:id?',
            name: 'purchaseOutboundDetail',
            hidden: true,
            props: true,
            component: () => lazyLoadView(import('@/views/sell/outbound/detail')),
            meta: {
                title: '销售出库详情',
                activeMenu: '/document/outbound/order',
                noCache: true,
                isDetailPage: true
            }
        }
    ]
}

export default router
