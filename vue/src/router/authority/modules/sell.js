/*路由表：销售管理*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/sell',
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
            path: 'outbound',
            name: 'sellOutbound',
            component: () => lazyLoadView(import('@/views/sell/outbound')),
            meta: {title: '销售出库'}
        }
    ]
}

export default router
