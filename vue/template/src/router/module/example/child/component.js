export default {
    path: 'component',
    meta: {title: '组件', icon: 'el-icon-s-grid'},
    children: [
        {
            path: 'upload',
            component: 'example/component/upload',
            meta: {title: '上传文件'}
        },
        {
            path: 'skeleton',
            component: 'example/component/skeleton',
            meta: {title: '骨架屏'}
        },
        {
            path: 'regionSelector',
            component: 'example/component/regionSelector',
            meta: {title: '行政区划选择'}
        },
        {
            path: 'treeSelect',
            component: 'example/component/treeSelect',
            meta: {title: '树选择'}
        }
    ]
}
