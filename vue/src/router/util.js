import {pathToRegexp} from 'path-to-regexp'
import {isEmpty} from "@/utils"
import {title} from "@/config"

//拼接页面标题
export function getPageTitle(pageTitle) {
    return pageTitle ? `${pageTitle} - ${title}` : title
}

//将给定的白名单url转换为正则
export function transformWhiteList(list) {
    return list.map(url => pathToRegexp(url))
}

//子路由继承父路由meta上的{affix,noAuth,noCache}，优先使用子路由的值
export function metaExtend(routes, meta) {
    routes.forEach(route => {
        if (meta) {
            const keys = ['affix', 'noAuth', 'noCache']
            Object.keys(meta).forEach(key => {
                if (keys.includes(key) && !isEmpty(meta[key]) && isEmpty(route.meta[key])) {
                    route.meta[key] = meta[key]
                }
            })
        }
        if (route.children) {
            metaExtend(route.children, route.meta)
        }
    })
}
