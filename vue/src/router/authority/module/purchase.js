/*路由表：采购管理*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/doc/purchase',
    component: Layout,
    meta: {title: '采购管理', icon: 'shopping', alwaysShow: true},
    children: [
        {
            path: 'order',
            name: 'purchaseOrder',
            component: lazyLoadView(import('@/view/purchase/order')),
            meta: {title: '采购订单'}
        },
        {
            path: 'order/detail/:type(see|add|edit)/:id?',
            name: 'purchaseOrderDetail',
            props: true,
            component: lazyLoadView(import('@/view/purchase/order/detail')),
            meta: {
                dynamicTitle(to) {
                    const {type, id} = to.params
                    switch (type) {
                        case 'add':
                            return '添加采购订单'
                        case 'edit':
                            return `编辑采购订单${id}`
                        case 'see':
                            return `查看采购订单${id}`
                    }
                },
                activeMenu: '/doc/purchase/order',
                usePathKey: true,
                commonModule: '@/view/purchase/order/detail'
            }
        },
        {
            path: 'inbound',
            name: 'purchaseInbound',
            component: lazyLoadView(import('@/view/purchase/inbound')),
            meta: {title: '采购入库'}
        },
        {
            path: 'inbound/detail/:type(see|add|edit)/:id?',
            name: 'purchaseInboundDetail',
            props: true,
            component: lazyLoadView(import('@/view/purchase/inbound/detail')),
            meta: {
                dynamicTitle(to) {
                    const {type, id} = to.params
                    switch (type) {
                        case 'add':
                            return '添加采购入库单'
                        case 'edit':
                            return `编辑采购入库单${id}`
                        case 'see':
                            return `查看采购入库单${id}`
                    }
                },
                activeMenu: '/doc/purchase/inbound',
                usePathKey: true,
                commonModule: '@/view/purchase/inbound/detail'
            }
        }
    ]
}

export default router
