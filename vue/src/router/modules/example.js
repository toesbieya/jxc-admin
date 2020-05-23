/*路由表：演示用例*/
import Layout from '@/layout'

const router = {
    path: '/example',
    component: Layout,
    meta: {title: '演示用例', icon: 'show', noCache: true, breadcrumb: false},
    children: [
        {
            path: 'style-page',
            name: 'stylePage',
            component: () => import('@/views/example/stylePage'),
            meta: {title: '样 式'}
        },
        {
            path: 'icons',
            name: 'icons',
            component: () => import('@/views/example/icons'),
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
                    component: () => import('@/views/example/cool/fluid'),
                    meta: {title: '流体动画'}
                },
                {
                    path: 'l2d',
                    name: 'l2d',
                    component: () => import('@/views/example/cool/l2d'),
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
                    path: 'upload',
                    name: 'upload',
                    component: () => import('@/views/example/components/upload'),
                    meta: {title: '上传文件'}
                },
                {
                    path: 'picture-preview',
                    name: 'picturePreview',
                    component: () => import('@/views/example/components/picturePreview'),
                    meta: {title: '图片预览'}
                },
                {
                    path: 'skeleton',
                    name: 'skeleton',
                    component: () => import('@/views/example/components/skeleton'),
                    meta: {title: '骨架屏'}
                },
                {
                    path: 'ripple',
                    name: 'ripple',
                    component: () => import('@/views/example/components/ripple'),
                    meta: {title: '波纹'}
                }
            ]
        },
        {
            path: 'developing-test',
            name: 'developingTest',
            component: () => import('@/views/example/developingTest'),
            meta: {title: '开发测试'}
        }
    ]
}

export default router
