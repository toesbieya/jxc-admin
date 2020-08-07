/**
 * 不需要权限控制的路由表
 * constant/module下的所有route都会加上noAuth:true
 */

const modulesFiles = require.context('./module', false, /\.js$/)
const modules = modulesFiles.keys().reduce((modules, modulePath) => {
    const value = modulesFiles(modulePath).default
    Array.isArray(value) ? modules.push(...value) : modules.push(value)
    return modules
}, [])

function addNoAuth(routes) {
    routes.forEach(route => {
        if (!route.meta) route.meta = {}
        route.meta.noAuth = true
        if (Array.isArray(route.children)) addNoAuth(route.children)
    })
}

addNoAuth(modules)

export default modules
