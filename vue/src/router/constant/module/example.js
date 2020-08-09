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
            component: lazyLoadView(import('@/view/example/stylePage')),
            meta: {title: '样 式'}
        },
        {
            path: 'icon',
            component: lazyLoadView(import('@/view/example/icon/indexPage')),
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
                    component: lazyLoadView(import('@/view/example/cool/fluidPage')),
                    meta: {title: '流体动画'}
                },
                {
                    path: 'l2d',
                    component: lazyLoadView(import('@/view/example/cool/l2dPage')),
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
                    component: lazyLoadView(import('@/view/example/component/uploadPage')),
                    meta: {title: '上传文件'}
                },
                {
                    path: 'skeletonExample',
                    component: lazyLoadView(import('@/view/example/component/skeletonPage')),
                    meta: {title: '骨架屏'}
                },
                {
                    path: 'rippleExample',
                    component: lazyLoadView(import('@/view/example/component/ripplePage')),
                    meta: {title: '波纹'}
                },
                {
                    path: 'signatureExample',
                    component: lazyLoadView(import('@/view/example/component/signaturePage')),
                    meta: {title: '手写签名'}
                },
                {
                    path: 'regionSelectorExample',
                    component: lazyLoadView(import('@/view/example/component/regionSelectorPage')),
                    meta: {title: '行政区划选择'}
                },
                {
                    path: 'treeSelectExample',
                    component: lazyLoadView(import('@/view/example/component/treeSelectPage')),
                    meta: {title: '树选择'}
                }
            ]
        },
        {
            path: 'developingTest',
            component: lazyLoadView(import('@/view/example/developingTest/indexPage')),
            meta: {title: '开发测试'}
        },
        {
            path: 'detailPage1',
            component: lazyLoadView(import('@/view/example/detailPage')),
            meta: {
                title: '详情页缓存1',
                usePathKey: true,
                commonModule: '@/view/example/detailPage'
            }
        },
        {
            path: 'detailPage2',
            component: lazyLoadView(import('@/view/example/detailPage')),
            meta: {
                title: '详情页缓存2',
                usePathKey: true,
                commonModule: '@/view/example/detailPage'
            }
        }
    ]
}

export default router
