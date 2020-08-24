/*路由表：演示用例*/

const router = {
    path: '/example',
    component: 'Layout',
    meta: {title: '演示用例', icon: 'svg-show', noAuth: true, noCache: true},
    children: [
        {
            path: 'icon',
            component: 'example/icon/',
            meta: {title: '图 标'}
        },
        {
            path: 'taobao',
            meta: {title: '淘宝iframe', iframe: 'https://www.taobao.com'}
        },
        {
            path: 'cool',
            meta: {title: '好玩的东东'},
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
        },
        {
            path: 'components',
            meta: {title: '组件'},
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
        },
        {
            path: 'developingTest',
            component: 'example/developingTest/',
            meta: {title: '开发测试'}
        },
        {
            path: 'cacheTest',
            meta: {title: '详情页缓存测试', noCache: false},
            children: [
                {
                    path: 'detailPage1',
                    component: 'example/cacheTest/detail',
                    meta: {
                        title: '详情页1',
                        usePathKey: true,
                        commonModule: '@/view/example/cacheTest/detailPage'
                    }
                },
                {
                    path: 'detailPage2',
                    component: 'example/cacheTest/detail',
                    meta: {
                        title: '详情页2',
                        usePathKey: true,
                        commonModule: '@/view/example/cacheTest/detailPage'
                    }
                }
            ]
        }
    ]
}

export default router
