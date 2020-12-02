export default {
    path: 'test',
    meta: {title: '测试页面', icon: 'el-icon-warning', alwaysShow: true},
    children: [
        {
            path: 'cache',
            meta: {title: '详情页缓存测试', noCache: false},
            children: [
                {
                    path: 'detailPage1',
                    component: 'example/test/cache/detail',
                    meta: {
                        title: '详情页1',
                        usePathKey: true,
                        commonModule: '@/view/example/test/cache/detailPage'
                    }
                },
                {
                    path: 'detailPage2',
                    component: 'example/test/cache/detail',
                    meta: {
                        title: '详情页2',
                        usePathKey: true,
                        commonModule: '@/view/example/test/cache/detailPage'
                    }
                }
            ]
        }
    ]
}
