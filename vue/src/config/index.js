const isDev = process.env.NODE_ENV === 'development'

const contextPath = '/'

module.exports = {
    isDev,

    title: 'BiuBiuBiu~',

    //项目的部署路径，始终以'/'开头，以'/'结束
    contextPath,

    //全局axios的baseUrl、devServer的路由前缀
    apiPrefix: '/api',

    //路由模式，['hash','history']
    routerMode: 'history',

    //是否在开发时使用mock，为true时不会启动websocket和代理
    useMock: false,

    //socket连接地址
    socketUrl: isDev ? 'localhost:12580' : 'wss://toesbieya.cn',

    //侧边栏顶上的logo地址
    sidebarLogoUrl: 'https://wpimg.wallstcn.com/69a1c46c-eb1c-4b46-8bd4-e9e686ef5251.png',

    errorLog: true,

    //storage的相关键名
    sessionUserKey: 'GCC-SESS-USER',
    localResourceKey: 'GCC-LOCAL-PERM',
    localPersonalSettingsKey: 'GCC-LOCAL-PERSONAL-SETTINGS',

    //路由切换时的过渡动画名称,关联transition.css
    rightSideRouteTransition: 'left-out',
    leftSideRouteTransition: 'right-out',

    //七牛云上传地址
    attachmentUploadUrl: 'https://upload.qiniup.com',

    //附件上传后的存储地址前缀
    attachmentPrefix: 'https://static.toesbieya.cn/',

    //文件预览接口前缀
    filePreviewPrefix: isDev ? 'http://localhost:8012' : 'https://preview.toesbieya.cn',

    //省市地区json数据请求地址
    regionDataUrl: `${contextPath}static/json/region-pca.json`
}
