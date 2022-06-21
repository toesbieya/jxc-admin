const path = require('path')
const sass = require('sass')
const { defineConfig } = require('@vue/cli-service')
const settings = require('./src/config')
const isDev = process.env.NODE_ENV === 'development'

function resolve(dir) {
  return path.join(__dirname, dir)
}

module.exports = defineConfig({
  publicPath: settings.contextPath,
  outputDir: 'dist',
  assetsDir: 'static',
  runtimeCompiler: true,
  lintOnSave: false,
  productionSourceMap: false,
  transpileDependencies: false,
  css: {
    loaderOptions: {
      css: {
        modules: {
          auto: /var.scss$/
        }
      },
      sass: {
        // https://github.com/sass/dart-sass/issues/1388#issuecomment-916125648
        sassOptions: {
          outputStyle: 'expanded',
          logger: sass.Logger.silent
        }
      }
    }
  },
  configureWebpack: {
    name: settings.title,
    resolve: {
      alias: {
        '@': resolve('src'),
        '@ele': resolve('element-ui-personal')
      }
    },
    devServer: {
      port: process.env.port || 8079,
      client: {
        overlay: {
          warnings: true,
          errors: true
        }
      },
      proxy:
        settings.useMock
          ? null
          : {
            [settings.apiPrefix]: {
              target: 'http://localhost:8081',
              ws: true,
              secure: false,
              changeOrigin: true,
              pathRewrite: {
                [`^${settings.apiPrefix}`]: ''
              }
            }
          },
      setupMiddlewares(middlewares, devServer) {
        if (settings.useMock) {
          require('./mock')(devServer.app)
        }
        return middlewares
      }
    }
  },
  chainWebpack(config) {
    config.plugins.delete('preload')
    config.plugins.delete('prefetch')

    // set svg-sprite-loader
    config
      .module
      .rule('svg')
      .exclude.add(resolve('src/asset/icon'))
      .end()
    config
      .module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/asset/icon'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({ symbolId: 'icon-[name]' })
      .end()

    config.when(!isDev, config => {
      //将css合并到一个文件中
      //https://github.com/vuejs/vue-cli/issues/2843
      config
        .optimization
        .removeEmptyChunks(true)
        .splitChunks({
          cacheGroups: {
            style: {
              name: 'style',
              type: 'css/mini-extract',
              chunks: 'all'
            }
          }
        })
        .end()
    })
  }
})
