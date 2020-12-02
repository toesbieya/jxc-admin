import NProgress from 'nprogress'

NProgress.configure({showSpinner: false})

const beforeEach = (to, from, next) => {
    //使用redirect进行跳转时不显示进度条
    !to.path.startsWith('/redirect') && NProgress.start()

    next()
}

const afterEach = () => NProgress.done()

export default function (router) {
    router.beforeEach(beforeEach)
    router.afterEach(afterEach)
}
