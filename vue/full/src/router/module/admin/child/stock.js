/*路由表：库存管理*/

const router = {
    path: 'stock',
    meta: {title: '库存管理', icon: 'svg-stock'},
    children: [
        {
            path: 'current',
            name: 'currentStock',
            component: 'admin/stock/current/',
            meta: {title: '当前库存'}
        }
    ]
}

export default router
