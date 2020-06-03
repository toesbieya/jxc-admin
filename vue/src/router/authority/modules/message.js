/*路由表：消息中心*/
import Layout from '@/layout'

const router = {
    path: '/message',
    component: Layout,
    alwaysShow: true,
    meta: {title: '消息中心', icon: 'message'},
    children: [
        {
            path: 'manage',
            name: 'messageManagement',
            component: () => import('@/views/message/manage'),
            meta: {title: '消息管理'}
        },
        {
            path: 'user',
            name: 'userMessage',
            component: () => import('@/views/message/user'),
            meta: {title: '个人消息', noAuth: true, noCache: true}
        }
    ]
}

export default router
