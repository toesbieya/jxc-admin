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
   `record` 必须要在**其他的web-modules**前启动，`account` 必须要在 `system` 之后启动，
   `document` 必须要在 `stock` 之后启动，剩余的可以按任意顺序启动。
