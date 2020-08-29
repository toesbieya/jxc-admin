import {mutations as iframeMutations} from "@/layout/store/iframe"

const beforeEach = (to, from, next) => {
    //从iframe页面离开时，判断是否需要删除iframe
    if (from.meta.iframe) {
        let del = false
        //如果设置了无缓存或是进行了刷新，那么移除iframe
        if (from.meta.noCache || to.path === `/redirect${from.path}`) {
            del = true
        }
        iframeMutations.close({src: from.meta.iframe, del})
    }

    //跳转至iframe页面时，打开iframe
    if (to.meta.iframe) {
        iframeMutations.open({src: to.meta.iframe})
    }

    next()
}

export default function (router) {
    router.beforeEach(beforeEach)
}
