export default {
    path: 'global',
    meta: {title: '全局方法', icon: 'el-icon-s-opportunity'},
    children: [
        {
            path: 'message',
            component: 'example/globalMethod/message',
            meta: {title: '消息提示'}
        },
        {
            path: 'bottomTip',
            component: 'example/globalMethod/bottomTip',
            meta: {title: '底部消息提示'}
        },
        {
            path: 'guide',
            component: 'example/globalMethod/guide',
            meta: {title: '导航'}
        },
        {
            path: 'imageViewer',
            component: 'example/globalMethod/imageViewer',
            meta: {title: '图片预览'}
        },
        {
            path: 'signature',
            component: 'example/globalMethod/signature',
            meta: {title: '手写签名'}
        },
        {
            path: 'verify',
            component: 'example/globalMethod/verify',
            meta: {title: '拼图验证'}
        }
    ]
}
