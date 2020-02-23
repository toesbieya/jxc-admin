/*路由表：有意思的组件*/
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
            meta: {title: '样 式', icon: 'style'}
        },
        {
            path: 'icons',
            name: 'icons',
            component: () => import('@/views/example/icons'),
            meta: {title: '图 标',icon: 'icon'}
        },
        {
            path: 'taobao',
            name: 'taobao',
            meta: {title: '淘宝iframe', iframe: 'https://www.taobao.com',icon: 'documentation'}
        },
        {
            path: 'cool',
            component: {render: h => h('router-view')},
            meta: {title: '好玩的东东',icon: 'cool'},
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
            meta: {title: '组件',icon: 'component'},
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
                    path: 'verify',
                    name: 'verify',
                    component: () => import('@/views/example/components/verify'),
                    meta: {title: '滑块验证'}
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
            meta: {title: '开发测试',icon: 'bug'}
        }
    ]
}

export default router
