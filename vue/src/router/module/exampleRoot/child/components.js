export default {
    path: 'component',
    meta: {title: '组件', icon: 'el-icon-s-grid'},
    children: [
        {
            path: 'uploadExample',
            component: 'example/component/upload',
            meta: {title: '上传文件'}
        },
        {
            path: 'skeletonExample',
            component: 'example/component/skeleton',
            meta: {title: '骨架屏'}
        },
        {
            path: 'rippleExample',
            component: 'example/component/ripple',
            meta: {title: '波纹'}
        },
        {
            path: 'signatureExample',
            component: 'example/component/signature',
            meta: {title: '手写签名'}
        },
        {
            path: 'regionSelectorExample',
            component: 'example/component/regionSelector',
            meta: {title: '行政区划选择'}
        },
        {
            path: 'treeSelectExample',
            component: 'example/component/treeSelect',
            meta: {title: '树选择'}
        }
    ]
}
