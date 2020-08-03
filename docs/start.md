# 启动

### 前端

```shell script
npm run serve
```

### 后端

local版直接运行启动类即可

cloud版请按以下步骤启动：

1. 下载并配制好nacos后，按照nacos的文档启动nacos。

2. 将`/nacos-config`下的文件打包成`DEFAULT_GROUP.ZIP`，然后在nacos的web端上导入。

3. 按一定顺序启动各个模块。`websocket`、`schedule`、`gateway` 这三个可以按任意顺序启动，
   之后启动 `web/file` 然后启动 `web/web-modules/record`，剩余的 `web-modules` 中，`web/web-modules/account` 
   必须要在 `web/web-modules/system` 之后启动，`web/web-modules/document` 必须要在 `web/web-modules/stock` 之后启动，
   剩余的可以按任意顺序启动。
