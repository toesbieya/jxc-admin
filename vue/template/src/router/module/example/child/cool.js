export default {
    path: 'cool',
    meta: {title: '好玩的东东', icon: 'el-icon-s-opportunity'},
    children: [
        {
            path: 'fluid',
            component: 'example/cool/fluid',
            meta: {title: '流体动画'}
        },
        {
            path: 'l2d',
            component: 'example/cool/l2d',
            meta: {title: '看板娘'}
        },
    ]
}
