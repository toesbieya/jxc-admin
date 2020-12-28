const isDev = process.env.NODE_ENV === 'development'

module.exports = {
    isDev,

    //项目的部署路径，始终以'/'开头，以'/'结束
    contextPath: '/',

    //页面标题、登录注册页的标题
    title: 'BiuBiuBiu~',

    //logo地址
    logo: `/static/img/logo.svg`,

    //路由配置
    route: {
        //路由模式，['hash','history']
        mode: 'history'
    },

    //本地存储配置
    storage: {
        //键名前缀
        keyPrefix: 'GCC-',

        //读写时是否进行压缩的默认值
        zip: !isDev
    }
}
