export default {
    path: 'iframe',
    meta: {title: 'iframe', noCache: false, icon: 'el-icon-s-flag'},
    children: [
        {
            path: 'taobao',
            meta: {title: '淘宝', iframe: 'https://www.taobao.com'}
        },
        {
            path: 'baidu',
            meta: {title: '百度', iframe: 'https://www.baidu.com'}
        }
    ]
}
