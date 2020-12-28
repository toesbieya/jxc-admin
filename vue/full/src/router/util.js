import Layout from "@/layout"
import SkeletonPage from '@/view/_common/SkeletonPage'
import Page500 from '@/view/_app/500'
import {isEmpty} from "@/util"
import {isExternal, isString} from "@/util/validate"

//json字符串转路由配置
export function str2routeConfig(str) {
    const parseOpt = (key, value) => {
        if (key === 'dynamicTitle' && isString(value)) {
            return isEmpty(value) ? undefined : eval(`(${value})`)
        }
        return value
    }
    return JSON.parse(str, parseOpt)
}

//根据json数组生成路由
export function generateRoutes(jsonTree) {
    //将深度为2的节点合并为由其最末子节点组成的数组
    function mergeChildren(routes) {
        function fun(child, parent = {path: ''}, list = []) {
            const {children} = child
            if (!isEmpty(parent.path)) {
                child.path = `${parent.path}/${child.path}`
            }
            if (!children || !children.length) {
                list.push(child)
            }
            else {
                children.forEach(i => fun(i, child, list))
            }
            return list
        }

        routes.forEach(root => {
            const {children} = root
            if (children && children.length) {
                const list = []
                children.forEach(route => {
                    list.push(...fun(route))
                })
                root.children = list
            }
        })
    }

    //数据转换，同时剔除不合法的路由
    function transform(routes, depth = 1) {
        if (!Array.isArray(routes)) return

        for (let i = routes.length - 1; i >= 0; i--) {
            const {path, redirect, component, children, meta = {}} = routes[i]

            //剔除外链节点以及无component且非iframe的节点
            if (isExternal(path) || !component && !meta.iframe) {
                routes.splice(i, 1)
                continue
            }

            //清理一些空属性，避免vue-router出错
            ['name', 'component', 'meta'].forEach(key => {
                if (key in routes[i] && isEmpty(routes[i][key])) {
                    delete routes[i][key]
                }
            })

            //设置路由组件，当component是vue组件声明对象时，不会进行懒加载
            if (component) {
                if (isString(component) && component.toUpperCase() === 'LAYOUT') {
                    routes[i].component = Layout
                }
                else if (isString(component) || typeof component === 'function') {
                    routes[i].component = lazyLoadView(component)
                }
            }

            //先尝试递归
            transform(children, depth + 1)

            //叶子节点统一设置props = true，省的动态路由还要手动加
            if (!children || children.length <= 0) {
                routes[i].props = true
            }
            //仅在根节点未设置redirect时，将其第一个子节点的路径设为redirect
            else if (depth === 1 && isEmpty(redirect)) {
                const rootPath = path.endsWith('/') ? path : `${path}/`
                routes[i].redirect = `${rootPath}${children[0].path}`
            }
        }
    }

    //路由页面懒加载，入参可以是字符串、返回值为Promise的函数
    function lazyLoadView(component) {
        if (isString(component)) {
            if (component.endsWith('/')) {
                component += 'index'
            }
            else if (component.endsWith('Page')) {
                component = component.replace('Page', '')
            }
            else {
                component = component.replace('Page.vue', '')
            }
        }

        //这里注意一点，如果设置了超时时间，那么超时后只能刷新整个页面才能重新加载该组件
        const AsyncHandler = () => ({
            component: typeof component === 'function' ? component() : import(`@/view/${component}Page.vue`),
            loading: SkeletonPage,
            error: Page500,
            delay: 200,
            timeout: 10000
        })

        return () => Promise.resolve({
            abstract: true,
            functional: true,
            render(h, {data, children}) {
                return h(AsyncHandler, data, children)
            }
        })
    }

    mergeChildren(jsonTree)
    transform(jsonTree)

    return jsonTree
}

//子路由继承父路由meta上的{noAuth, noCache}，优先使用子路由的值
export function metaExtend(routes, parentMeta) {
    const keys = ['noAuth', 'noCache']
    routes.forEach(route => {
        if (parentMeta) {
            Object.keys(parentMeta).forEach(key => {
                const value = parentMeta[key]
                if (keys.includes(key) && !isEmpty(value)) {
                    if (!route.meta) {
                        route.meta = {[key]: value}
                    }
                    else if (isEmpty(route.meta[key])) {
                        route.meta[key] = value
                    }
                }
            })
        }
        route.children && metaExtend(route.children, route.meta)
    })
}

//将require.context导出的对象处理为数组
export function context2array(context) {
    return context.keys().reduce((modules, modulePath) => {
        const value = context(modulePath).default
        Array.isArray(value) ? modules.push(...value) : modules.push(value)
        return modules
    }, [])
}
