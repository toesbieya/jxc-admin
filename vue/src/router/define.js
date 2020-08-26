import Layout from "@/layout/index"
import Redirect from "@/view/app/redirect"
import Login from "@/view/app/login/index"
import Page404 from "@/view/app/404"
import Page403 from "@/view/app/403"
import routes from './module'
import {parseRoutes, stringifyRoutes} from "./util"

export function getDynamicRoutes() {
    return parseRoutes(stringifyRoutes(routes))
}

//系统自有路由
export default [
    {
        path: '/redirect',
        component: Layout,
        children: [
            {
                path: ':path(.*)',
                props: true,
                component: Redirect
            }
        ]
    },
    {
        path: '/login',
        alias: '/register',
        component: Login
    },
    {
        path: '/404',
        component: Page404
    },
    {
        path: '/403',
        component: Page403
    }
]
