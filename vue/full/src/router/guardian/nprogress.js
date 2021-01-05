import NProgress from 'nprogress'
import {Const} from 'el-admin-layout'

NProgress.configure({showSpinner: false})

const beforeEach = (to, from, next) => {
    //使用redirect进行跳转时不显示进度条
    !to.path.startsWith(Const.redirectPath) && NProgress.start()

    next()
}

const afterEach = () => NProgress.done()

export default function (router) {
    router.beforeEach(beforeEach)
    router.afterEach(afterEach)
}
