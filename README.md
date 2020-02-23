<p align="center">
  <img width="100" src="https://wpimg.wallstcn.com/69a1c46c-eb1c-4b46-8bd4-e9e686ef5251.png">
</p>

## 简介

一个前后端分离的简易进销存后台管理系统，基于 [SpringBoot](https://spring.io/projects/spring-boot/) 和 [vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)实现，具备常见的后台管理功能，登录态使用session，使用基于资源url的简单权限控制。

- [在线预览](https://toesbieya.cn)

## 开发

**前端没有写mock，必须搭配后端启动。**

**后端依赖jdk8，mysql8.0，redis3.2，请确保本地具备以上环境。初次安装时请运行后端项目的初始化sql脚本**

**大部分情况下，你只需要修改后端的application.yml里的mysql、redis、七牛配置项，前端需要修改config/index.js里的七牛云相关配置项即可**

##前端说明

- 基于[vue-element-admin](https://github.com/PanJiaChen/vue-element-admin) 4.2.1，功能做了大量调整，具体如下：
```
- 样式修改
    - 标签页tagsView右键菜单样式风格改为和侧边栏一样
    - 对ele的样式做了修改，具体可以查看assets/style/element-ui.scss，或者在预览页面里查看`演示用例->样式`
- 移除响应式功能（复杂的后台管理想做成像VuePress那样的响应式不现实）
- 布局修改
    - 经典后台布局改用flex实现
    - 主页面使用el-scrollbar包裹
    - 增加了ele的backToTop组件
- 路由修改
    - 修改部分路由配置项，详细请看代码备注
    - addRoutes时不判断权限，添加全部路由，然后在全局路由守卫内判断是否有权限进入页面
- 组件修改
    - 侧边栏、导航栏增加了一些显示效果，具体可以在个性化设置内查看
    - 标签页tagsView改造，加入了快捷键切换、拖拽排序、双击关闭功能，平滑滚动，修复关闭所有后路由未跳转的bug
    - 面包屑改成样子货，纯粹展示当前路由路径
```

- 目录结构
```
├── mock                       # 项目mock，有需要的可以自己写，然后在vue.config.js里配置devServer
├── public
├── src
│   ├── api
│   ├── assets
│   │   │── icons              # 项目所有 svg icons
│   │   └── styles             # 全局样式
│   ├── bizComponents          # 全局公用业务组件
│   ├── components             # 全局公用组件
│   ├── config
│   │   │── index.js           # 基础配置项
│   │   └── request.js         # axios封装
│   ├── directive
│   ├── filters
│   ├── layout
│   ├── mixins                 # 公用混入
│   ├── plugin                 # 一些插件
│   ├── router
│   ├── store
│   ├── utils
│   ├── views
│   ├── App.vue
│   └── main.js
```
