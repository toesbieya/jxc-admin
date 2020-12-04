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
            path: 'imageViewer',
            component: 'example/globalMethod/imageViewer',
            meta: {title: '图片预览'}
        },
        {
            path: 'verify',
            component: 'example/globalMethod/verify',
            meta: {title: '拼图验证'}
        }
    ]
}
