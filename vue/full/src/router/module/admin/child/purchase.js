/*路由表：采购管理*/

const router = {
    path: 'purchase',
    meta: {title: '采购管理', icon: 'svg-shopping'},
    children: [
        {
            path: 'order',
            name: 'purchaseOrder',
            component: 'admin/purchase/order/',
            meta: {title: '采购订单'}
        },
        {
            path: 'order/detail/:type(see|add|edit)/:id?',
            component: 'admin/purchase/order/detail',
            meta: {
                title: '采购订单详情页',
                hidden: true,
                pageHeader: false,
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
                activeMenu: '/purchase/order',
                usePathKey: true
            }
        },
        {
            path: 'inbound',
            name: 'purchaseInbound',
            component: 'admin/purchase/inbound/',
            meta: {title: '采购入库'}
        },
        {
            path: 'inbound/detail/:type(see|add|edit)/:id?',
            component: 'admin/purchase/inbound/detail',
            meta: {
                title: '采购入库详情页',
                hidden: true,
                pageHeader: false,
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
                activeMenu: '/purchase/inbound',
                usePathKey: true
            }
        }
    ]
}

export default router
