module.exports = {
    title: 'jxc-admin',

    description: "jxc-admin后台管理系统文档",

    host: 'localhost',

    // 输出目录
    dest: 'dist',

    // 主题配置
    themeConfig: {
        smoothScroll: true,

        lastUpdated: '上次更新：',

        nav: [
            {text: '在线预览', link: 'https://toesbieya.cn'},
            {text: 'GitHub', link: 'https://github.com/toesbieya/jxc-admin'}
        ],

        sidebar: [
            'installation',
            'start',
            {
                title: '前端',
                collapsable: false,
                children: [
                    '/frontend/',
                    '/frontend/layout',
                    '/frontend/router',
                    '/frontend/permission',
                    '/frontend/new',
                    '/frontend/deploy',
                    {
                        title: '组件',
                        collapsable: false,
                        children: [
                            '/frontend/components/abstract-table',
                            '/frontend/components/region-selector',
                            '/frontend/components/search-form',
                            '/frontend/components/upload-file',
                        ]
                    },
                    {
                        title: '全局方法',
                        collapsable: false,
                        children: [
                            '/frontend/global-methods/bottom-tip',
                            '/frontend/global-methods/guide',
                            '/frontend/global-methods/image-viewer',
                            '/frontend/global-methods/puzzle-verify',
                            '/frontend/global-methods/signature',
                        ]
                    },
                    '/frontend/utils',
                    '/frontend/plugins',
                    '/frontend/mock'
                ]
            },
            {
                title:'后端',
                collapsable: false,
                children: [
                    '/backend/',
                ]
            }
        ]
    },

    plugins: {
        '@vuepress/pwa': {
            serviceWorker: true,
            updatePopup: true
        },
        '@vuepress/back-to-top': true
    }
}
