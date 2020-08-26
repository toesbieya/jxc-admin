/*路由表：消息中心*/

const router = {
    path: 'message',
    meta: {title: '消息中心', icon: 'svg-message', alwaysShow: true},
    children: [
        {
            path: 'manage',
            name: 'messageManagement',
            component: 'message/manage/',
            meta: {title: '消息管理'}
        },
        {
            path: 'user',
            name: 'userMessage',
            component: 'message/user/',
            meta: {title: '个人消息', noAuth: true, noCache: true}
        }
    ]
}

export default router
