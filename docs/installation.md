# 安装

## 环境准备

- jdk 1.8
- mysql 8
- redis 3.2+
- node
- npm
- nacos 1.3.1-BETA（如果是local版则不需要，[下载地址](https://github.com/alibaba/nacos/releases)）

::: warning 注意
后端开发时ide需要安装lombok插件
:::

## 基础信息配置

- 新建一个数据库，导入位于`/java/db`目录下的sql脚本（初始管理员账号密码：admin/123456）

- 修改后端配置中的`datasource`、`redis`、`socket`、`qiniu`等配置项，
  local版的配置文件是`java/local/src/main/resources/application.yml`，
  cloud版的配置文件是各个模块的`boostrap.yml`以及`java/cloud/nacos-config`下的文件

- 根据注释修改`vue/src/config/index.js`中的变量，
  通常只需要修改`socketUrl`、`attachmentPrefix`、`filePreviewPrefix`即可

::: warning 注意
如果后端部署到服务器上，socket配置中的hostname需要是服务器的局域网ip，否则socket服务端创建不了
:::
