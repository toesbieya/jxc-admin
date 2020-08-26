/*路由表：销售管理*/

const router = {
    path: 'sell',
    meta: {title: '销售管理', icon: 'svg-sell', alwaysShow: true},
    children: [
        {
            path: 'order',
            name: 'sellOrder',
            component: 'sell/order/',
            meta: {title: '销售订单'}
        },
        {
            path: 'order/detail/:type(see|add|edit)/:id?',
            props: true,
            component: 'sell/order/detail',
            meta: {
                title: '销售订单详情页',
                hidden: true,
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
                activeMenu: '/sell/order',
                usePathKey: true,
                commonModule: '@/view/sell/order/detail'
            }
        },
        {
            path: 'outbound',
            name: 'sellOutbound',
            component: 'sell/outbound/',
            meta: {title: '销售出库'}
        },
        {
            path: 'outbound/detail/:type(see|add|edit)/:id?',
            props: true,
            component: 'sell/outbound/detail',
            meta: {
                title: '销售出库详情页',
                hidden: true,
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
                activeMenu: '/sell/outbound',
                usePathKey: true,
                commonModule: '@/view/sell/outbound/detail'
            }
        }
    ]
}

export default router
