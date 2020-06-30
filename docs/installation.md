# 安装

## 环境准备

- jdk1.8
- mysql8
- redis3.2
- node
- npm

::: warning 注意
后端开发时ide需要安装lombok插件
:::

## 基础信息配置

- 导入位于`java`目录下的sql脚本（初始管理员账号密码：admin/123456）

- 修改`java/src/main/resources/application.yml`中的`datasource`、`redis`、`socket`、`file`、`qiniu`等配置项

- 根据注释修改`vue/src/config/index.js`中的变量，通常只需要修改`socketUrl`、`attachmentPrefix`、`filePreviewPrefix`即可

::: warning 注意
如果后端部署到服务器上，yml中的socket.hostname需要是服务器的局域网ip，否则socket服务端创建不了
:::
