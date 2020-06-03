'use strict'
const path = require('path')
const settings = require('./src/config')
const CompressionWebpackPlugin = require('compression-webpack-plugin')
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin

function resolve(dir) {
    return path.join(__dirname, dir)
}

const port = process.env.port || 8079 // dev port

// All configuration item explanations can be find in https://cli.vuejs.org/config/
module.exports = {
    publicPath: settings.routerMode === 'history' ? '/' : './',
    outputDir: 'dist',
    assetsDir: 'static',
    runtimeCompiler: true,
    lintOnSave: false,
    productionSourceMap: process.env.NODE_ENV === 'development',
    parallel: true,
    devServer: {
        port,
        open: true,
        overlay: {
            warnings: true,
            errors: true
        },
        proxy: {
            [settings.apiPrefix]: {
                target: 'http://localhost:8081',  // 后台接口域名
                ws: true,        //如果要代理 websockets，配置这个参数
                secure: false,  // 如果是https接口，需要配置这个参数
                changeOrigin: true,  //是否跨域
                pathRewrite: {
                    [`^${settings.apiPrefix}`]: ''
                }
            }
        },
        /*before(app) {
            require('./mock')(app)
        }*/
    },
    configureWebpack: {
        name: settings.title,
        resolve: {
            alias: {
                '@': resolve('src')
            }
        },
        plugins: [
            new CompressionWebpackPlugin({
                filename: '[path].gz[query]',
                algorithm: 'gzip',
                test: new RegExp('\\.(js|css)$'),//匹配文件名
                threshold: 10240,//对10K以上的数据进行压缩
                minRatio: 0.8,
                deleteOriginalAssets: false,//是否删除源文件
            }),
            process.env.NODE_ENV === 'production' ? new BundleAnalyzerPlugin() : {apply: () => ({})}
        ]
    },
    chainWebpack(config) {
        config.plugins.delete('preload')
        config.plugins.delete('prefetch')

        // set svg-sprite-loader
        config.module
            .rule('svg')
            .exclude.add(resolve('src/assets/icons'))
            .end()
        config.module
            .rule('icons')
            .test(/\.svg$/)
            .include.add(resolve('src/assets/icons'))
            .end()
            .use('svg-sprite-loader')
            .loader('svg-sprite-loader')
            .options({symbolId: 'icon-[name]'})
            .end()

        // set preserveWhitespace
        config.module
            .rule('vue')
            .use('vue-loader')
            .loader('vue-loader')
            .tap(options => {
                options.compilerOptions.preserveWhitespace = true
                return options
            })
            .end()

        config
            .when(process.env.NODE_ENV !== 'development',
                config => {
                    config
                        .plugin('ScriptExtHtmlWebpackPlugin')
                        .after('html')
                        .use('script-ext-html-webpack-plugin', [{inline: /runtime\..*\.js$/}])
                        .end()
                    config
                        .optimization.splitChunks({
                        chunks: 'all',
                        cacheGroups: {
                            libs: {
                                name: 'chunk-libs',
                                test: /[\\/]node_modules[\\/]/,
                                priority: 10,
                                chunks: 'initial' // only package third parties that are initially dependent
                            },
                            elementUI: {
                                name: 'chunk-elementUI', // split elementUI into a single package
                                priority: 20, // the weight needs to be larger than libs and app or it will be packaged into libs or app
                                test: /[\\/]node_modules[\\/]_?element-ui(.*)/ // in order to adapt to cnpm
                            },
                            commons: {
                                name: 'chunk-commons',
                                test: resolve('src/components'), // can customize your rules
                                minChunks: 3, //  minimum common number
                                priority: 5,
                                reuseExistingChunk: true
                            }
                        }
                    })
                    config.optimization.runtimeChunk('single')
                }
            )
    },
    css: {
        loaderOptions: {
            sass: {prependData: `@import "src/assets/styles/variables.scss";`,}
        }
    }
}
