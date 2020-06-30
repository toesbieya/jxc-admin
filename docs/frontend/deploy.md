# 构建和发布

## 构建

当项目开发完毕，只需要运行一行命令就可以打包你的应用：
```shell script
# 默认环境为 production
npm run build
```

构建打包成功之后，会在根目录生成`dist`文件夹，里面就是构建打包好的静态文件。

如果需要指定`dist`目录等，则需要通过`vue.config.js`的`outputDir`进行配置。

## 发布

如果静态文件不是部署在根目录下，比如想部署在 `/app` 下，那么需要将 `@/config/index.js` 中的 `contextPath` 改为 `/app/`。

想在 `js`中引入的话，可以直接使用 `process.env.BASE_URL` 拿到`contextPath`。

如果是在 `html` 中引入的话，是 `<%= BASE_URL %>` 的形式，这是 `ejs` 的语法。

::: warning 注意
`contextPath` 应始终以 `/` 开头，以 `/` 结束。
:::

## 路由模式

项目提供了动态修改路由模式的功能，只需要修改 `@/config/index.js` 中的 `routerMode` 即可。

当路由模式为 `'history'` 时，需要服务器提供支持。以 `nginx` 为例，需要在配置中补上：
```shell script
try_files $uri $uri/ /index.html;
```
