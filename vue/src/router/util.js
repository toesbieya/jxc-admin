import PageSkeleton from "@/component/Skeleton/PageSkeleton"
import Layout from '@/layout'
import {isEmpty} from "@/util"
import {isExternal} from "@/util/validate"

//json字符串转路由
export function parseRoutes(str) {
    const parseOpt = (key, value) => {
        if (key === 'dynamicTitle' && typeof value === 'string') {
            return isEmpty(value) ? undefined : eval(`(${value})`)
        }
        return value
    }
    return JSON.parse(str, parseOpt)
}

//路由转json字符串
export function stringifyRoutes(routes) {
    const stringifyOpt = (key, value) => {
        if (key === 'dynamicTitle' && typeof value === 'function') {
            return value.toString()
        }
        return value
    }
    return JSON.stringify(routes, stringifyOpt)
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

    //数据转换，同时剔除不合法的路由(外链、无子节点且无component且非iframe)
    function transform(routes, depth = 1) {
        for (let i = routes.length - 1; i >= 0; i--) {
            const {path, component, children, meta = {}} = routes[i]

            if (isEmpty(component)) {
                //剔除外链
                if (isExternal(path)) {
                    routes.splice(i, 1)
                    continue
                }
                delete routes[i].component
            }
            else {
                //设置路由组件
                if (component.toUpperCase() === 'LAYOUT') {
                    routes[i].component = Layout
                }
                else routes[i].component = lazyLoadView(component)
            }

            if (!children || children.length <= 0) {
                //末级节点统一设置props=true，省的动态路由还要手动加
                routes[i].props = true
            }
            else {
                transform(children, depth + 1)

                //如果子级被清空，并且无component且非iframe
                if (children.length <= 0 && !meta.iframe) {
                    routes.splice(i, 1)
                    continue
                }

                //设置根节点的redirect
                if (depth === 1) {
                    let rootPath = path
                    if (!rootPath.endsWith('/')) rootPath += '/'
                    routes[i].redirect = `${rootPath}${children[0].path}`
                }
            }
        }
    }

    //路由页面懒加载
    function lazyLoadView(path) {
        if (path.endsWith('/')) path += 'index'
        else if (path.endsWith('Page')) path = path.replace('Page', '')
        else path = path.replace('Page.vue', '')

        const AsyncHandler = () => ({component: import(`@/view/${path}Page.vue`), loading: PageSkeleton})
        return () => Promise.resolve({
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

//子路由继承父路由meta上的{noAuth,noCache}，优先使用子路由的值
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
