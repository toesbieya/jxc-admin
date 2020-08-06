/*路由表：销售管理*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/doc/sell',
    component: Layout,
    alwaysShow: true,
    meta: {title: '销售管理', icon: 'sell'},
    children: [
        {
            path: 'order',
            name: 'sellOrder',
            component: lazyLoadView(import('@/view/sell/order')),
            meta: {title: '销售订单'}
        },
        {
            path: 'order/detail/:type(see|add|edit)/:id?',
            name: 'sellOrderDetail',
            props: true,
            component: lazyLoadView(import('@/view/sell/order/detail')),
            meta: {
                dynamicTitle(to) {
                    const {type, id} = to.params
                    switch (type) {
                        case 'add':
                            return '添加销售订单'
                        case 'edit':
                            return `编辑销售订单${id}`
                        case 'see':
                            return `查看销售订单${id}`
                    }
                },
                activeMenu: '/doc/sell/order',
                usePathKey: true,
                commonModule:'@/view/sell/order/detail'
            }
        },
        {
            path: 'outbound',
            name: 'sellOutbound',
            component: lazyLoadView(import('@/view/sell/outbound')),
            meta: {title: '销售出库'}
        },
        {
            path: 'outbound/detail/:type(see|add|edit)/:id?',
            name: 'purchaseOutboundDetail',
            props: true,
            component: lazyLoadView(import('@/view/sell/outbound/detail')),
            meta: {
                dynamicTitle(to) {
                    const {type, id} = to.params
                    switch (type) {
                        case 'add':
                            return '添加销售出库单'
                        case 'edit':
                            return `编辑销售出库单${id}`
                        case 'see':
                            return `查看销售出库单${id}`
                    }
                },
                activeMenu: '/doc/sell/outbound',
                usePathKey: true,
                commonModule: '@/view/sell/outbound/detail'
            }
        }
    ]
}

export default router
