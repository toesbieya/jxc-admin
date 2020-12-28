import {isEmpty} from "@/util"

const beforeEach = (to, from, next) => {
    //若是共用组件的路由页面之间的跳转，借助redirect避免组件复用
    const a = to.meta.commonModule, b = from.meta.commonModule
    const isComponentReuse = !isEmpty(a) && a === b
    if (isComponentReuse) {
        //这里vue-router会报redirect错误，已在下方忽略
        return next(`/redirect${to.fullPath}`)
    }
    next()
}

export default function (router) {
    router.beforeEach(beforeEach)
}

window.addEventListener('unhandledrejection', event => {
    if (event.reason.stack && event.reason.stack.startsWith('Error: Redirected when going from')) {
        event.preventDefault()
    }
})
