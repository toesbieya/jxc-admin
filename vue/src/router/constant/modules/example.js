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
            name: 'stylePage',
            component: lazyLoadView(import('@/views/example/stylePage')),
            meta: {title: '样 式'}
        },
        {
            path: 'icons',
            name: 'icons',
            component: lazyLoadView(import('@/views/example/icons')),
            meta: {title: '图 标'}
        },
        {
            path: 'taobao',
            name: 'taobao',
            meta: {title: '淘宝iframe', iframe: 'https://www.taobao.com'}
        },
        {
            path: 'cool',
            component: {render: h => h('router-view')},
            meta: {title: '好玩的东东'},
            children: [
                {
                    path: 'fluid',
                    name: 'fluid',
                    component: lazyLoadView(import('@/views/example/cool/fluid')),
                    meta: {title: '流体动画'}
                },
                {
                    path: 'l2d',
                    name: 'l2d',
                    component: lazyLoadView(import('@/views/example/cool/l2d')),
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
                    name: 'uploadExample',
                    component: lazyLoadView(import('@/views/example/components/uploadExample')),
                    meta: {title: '上传文件'}
                },
                {
                    path: 'skeletonExample',
                    name: 'skeletonExample',
                    component: lazyLoadView(import('@/views/example/components/skeletonExample')),
                    meta: {title: '骨架屏'}
                },
                {
                    path: 'rippleExample',
                    name: 'rippleExample',
                    component: lazyLoadView(import('@/views/example/components/rippleExample')),
                    meta: {title: '波纹'}
                },
                {
                    path: 'signatureExample',
                    name: 'signatureExample',
                    component: lazyLoadView(import('@/views/example/components/signatureExample')),
                    meta: {title: '手写签名'}
                },
                {
                    path: 'regionSelectorExample',
                    name: 'regionSelectorExample',
                    component: lazyLoadView(import('@/views/example/components/regionSelectorExample')),
                    meta: {title: '行政区划选择'}
                },
                {
                    path: 'treeSelectExample',
                    name: 'treeSelectExample',
                    component: lazyLoadView(import('@/views/example/components/treeSelectExample')),
                    meta: {title: '树选择'}
                }
            ]
        },
        {
            path: 'developingTest',
            name: 'developingTest',
            component: lazyLoadView(import('@/views/example/developingTest')),
            meta: {title: '开发测试'}
        },
        {
            path: 'detailPage1',
            name: 'detailPage1',
            component: lazyLoadView(import('@/views/example/detailPage')),
            meta: {title: '详情页缓存1', noCache: false, isDetailPage: true}
        },
        {
            path: 'detailPage2',
            name: 'detailPage2',
            component: lazyLoadView(import('@/views/example/detailPage')),
            meta: {title: '详情页缓存2', noCache: false, isDetailPage: true}
        }
    ]
}

export default router
