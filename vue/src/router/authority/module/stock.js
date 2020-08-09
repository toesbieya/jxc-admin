/*路由表：库存管理*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/stock',
    component: Layout,
    meta: {title: '库存管理', icon: 'stock', alwaysShow: true},
    children: [
        {
            path: 'current',
            name: 'currentStock',
            component: lazyLoadView(import('@/view/stock/current/indexPage')),
            meta: {title: '当前库存'}
        }
    ]
}

export default router
