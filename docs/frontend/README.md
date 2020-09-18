# 介绍

这是在[vue-element-admin](https://github.com/PanJiaChen/vue-element-admin) 的基础上改造而来的。

## 功能

```
- 首屏加载动画

- 登录 \ 注册
  - 集成多种canvas背景动画
  - 仿极验滑动验证

- 动态路由、动态菜单

- 布局
  - 异步路由组件loading骨架屏
  - 多种导航模式
  - 仿ant design响应式的顶部菜单（虽然存在问题，目前推测是ele的问题）
  - 无限级递归菜单
  - 多页签（支持详情页缓存）
  - 支持移动端

- Excel导出，支持多级表头、合并行

- element-ui增强
  - message支持分组、进度条功能
```

## 目录结构
```
├── element-ui-personal        # element-ui增强
├── mock                       # 项目mock
├── public                     # 静态资源文件夹
├── src
│   ├── api                    # 数据请求集合
│   │   │── request.js         # axios封装
│   ├── asset
│   │   │── icon               # 项目所有svg图标
│   │   └── style              # 样式集合
│   ├── component              # 公用组件
│   ├── config
│   │   │── index.js           # 基础配置项
│   ├── directive              # 自定义指令集合
│   ├── filter                 # 自定义过滤器集合
│   ├── globalMethod           # 全局方法，已挂载到Vue原型上，使用this.$xx调用
│   ├── layout                 # 布局文件夹，包含了导航栏、侧边栏、多页签、全局页脚
│   ├── mixin                  # 公用混入
│   ├── plugin                 # 一些插件，均使用import()来动态引入
│   ├── router
│   │   │── guardian           # 路由守卫集合
│   │   │── module             # 前端定义的静态路由
│   │   │── define.js          # 静态路由导出
│   │   │── index.js
│   │   └── util.js
│   ├── store                  # vuex配置
│   ├── util                   # 工具类
│   ├── view                   # 路由页面集合
│   ├── App.vue
│   └── main.js
```
