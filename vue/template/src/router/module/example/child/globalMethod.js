export default {
    path: 'global',
    meta: {title: '全局方法', icon: 'el-icon-s-opportunity', alwaysShow: true},
    children: [
        {
            path: 'message',
            component: 'example/globalMethod/message',
            meta: {title: '消息提示'}
        }
    ]
}
