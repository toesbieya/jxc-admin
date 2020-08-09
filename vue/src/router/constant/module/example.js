/*路由表：演示用例*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/example',
    component: Layout,
    meta: {title: '演示用例', icon: 'show', noCache: true},
    children: [
        {
            path: 'stylePage',
            component: lazyLoadView('example/style'),
            meta: {title: '样 式'}
        },
        {
            path: 'icon',
            component: lazyLoadView('example/icon/'),
            meta: {title: '图 标'}
        },
        {
            path: 'taobao',
            meta: {title: '淘宝iframe', iframe: 'https://www.taobao.com'}
        },
        {
            path: 'cool',
            component: {render: h => h('router-view')},
            meta: {title: '好玩的东东'},
            children: [
                {
                    path: 'fluid',
                    component: lazyLoadView('example/cool/fluid'),
                    meta: {title: '流体动画'}
                },
                {
                    path: 'l2d',
                    component: lazyLoadView('example/cool/l2d'),
                    meta: {title: '看板娘'}
                },
            ]
        },
        {
            path: 'components',
            component: {render: h => h('router-view')},
            meta: {title: '组件'},
            children: [
                {
                    path: 'uploadExample',
                    component: lazyLoadView('example/component/upload'),
                    meta: {title: '上传文件'}
                },
                {
                    path: 'skeletonExample',
                    component: lazyLoadView('example/component/skeleton'),
                    meta: {title: '骨架屏'}
                },
                {
                    path: 'rippleExample',
                    component: lazyLoadView('example/component/ripple'),
                    meta: {title: '波纹'}
                },
                {
                    path: 'signatureExample',
                    component: lazyLoadView('example/component/signature'),
                    meta: {title: '手写签名'}
                },
                {
                    path: 'regionSelectorExample',
                    component: lazyLoadView('example/component/regionSelector'),
                    meta: {title: '行政区划选择'}
                },
                {
                    path: 'treeSelectExample',
                    component: lazyLoadView('example/component/treeSelect'),
                    meta: {title: '树选择'}
                }
            ]
        },
        {
            path: 'developingTest',
            component: lazyLoadView('example/developingTest/'),
            meta: {title: '开发测试'}
        },
        {
            path: 'detailPage1',
            component: lazyLoadView('example/detail'),
            meta: {
                title: '详情页缓存1',
                usePathKey: true,
                commonModule: '@/view/example/detailPage'
            }
        },
        {
            path: 'detailPage2',
            component: lazyLoadView('example/detail'),
            meta: {
                title: '详情页缓存2',
                usePathKey: true,
                commonModule: '@/view/example/detailPage'
            }
        }
    ]
}

export default router
