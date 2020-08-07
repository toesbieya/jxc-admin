/**
 * 不需要权限控制的路由表
 * constant/module下的所有route都会加上noAuth:true
 */
import Layout from '@/layout'
import {lazyLoadView} from '@/router/util'

const modulesFiles = require.context('./module', false, /\.js$/)
const modules = modulesFiles.keys().reduce((modules, modulePath) => {
    const value = modulesFiles(modulePath).default
    Array.isArray(value) ? modules.push(...value) : modules.push(value)
    return modules
}, [])

const routes = [
    ...modules,
    {
        path: '/',
        component: Layout,
        redirect: '/index',
        children: [
            {
                path: 'index',
                component: lazyLoadView(import('@/view/index')),
                name: 'index',
                meta: {title: '首页', affix: true, icon: 'home', sort: 0}
            }
        ]
    },
    {
        path: 'https://doc.toesbieya.cn',
        meta: {title: '文档', icon: 'documentation', sort: 1}
    },
    {
        path: '/user',
        component: Layout,
        redirect: '/user/index',
        children: [
            {
                path: 'index',
                name: 'userCenter',
                component: lazyLoadView(import('@/view/userCenter')),
                meta: {title: '个人中心', noCache: true, icon: 'user', hidden: true},
            }
        ]
    }
]

function addNoAuth(routes) {
    routes.forEach(route => {
        if (!route.meta) route.meta = {}
        route.meta.noAuth = true
        if (Array.isArray(route.children)) addNoAuth(route.children)
    })
}

addNoAuth(routes)

export default routes
