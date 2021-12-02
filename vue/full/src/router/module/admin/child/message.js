/*路由表：消息中心*/

const router = {
    path: 'message',
    meta: {title: '消息中心', icon: 'svg-message'},
    children: [
        {
            path: 'manage',
            name: 'messageManagement',
            component: 'admin/message/manage/',
            meta: {title: '消息管理'}
        },
        {
            path: 'user',
            name: 'userMessage',
            component: 'admin/message/user/',
            meta: {title: '个人消息', noAuth: true, noCache: true}
        }
    ]
}

export default router
