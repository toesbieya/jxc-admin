/*路由表：消息中心*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/message',
    component: Layout,
    meta: {title: '消息中心', icon: 'message', alwaysShow: true},
    children: [
        {
            path: 'manage',
            name: 'messageManagement',
            component: lazyLoadView(import('@/view/message/manage')),
            meta: {title: '消息管理'}
        },
        {
            path: 'user',
            name: 'userMessage',
            component: lazyLoadView(import('@/view/message/user')),
            meta: {title: '个人消息', noAuth: true, noCache: true}
        }
    ]
}

export default router
