const isDev = process.env.NODE_ENV === 'development'

module.exports = {
    isDev,

    //项目的部署路径，始终以'/'开头，以'/'结束
    contextPath: '/',

    //页面标题、登录注册页的标题
    title: 'BiuBiuBiu~',

    //路由配置
    route: {
        //路由模式，['hash','history']
        mode: 'history',

        //路由过渡动画设置，关联transition.css
        animate: {
            //当未启用多页签时的路由动画
            default: 'el-fade-in-linear',
            //要访问的tab顺序高于上一个访问的tab时的路由动画
            next: 'left-out',
            //要访问的tab顺序不高于上一个访问的tab时的路由动画
            prev: 'right-out',
        }
    },

    //本地存储配置
    storage: {
        //键名前缀
        keyPrefix: 'GCC-',

        //读写时是否进行压缩的默认值
        zip: !isDev
    }
}
