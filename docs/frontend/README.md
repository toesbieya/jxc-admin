# 介绍

这是在[vue-element-admin](https://github.com/PanJiaChen/vue-element-admin) 的基础上改造而来的。

布局使用的是[el-admin-layout](https://github.com/toesbieya/el-admin-layout)

分为需要配合后端使用的full版（vue/full）和精简版（vue/template），以下说明均以full版为准。

## 功能

```
- 首屏加载动画

- 登录 \ 注册
  - 集成多种canvas背景动画
  - 仿极验滑动验证

- 动态路由、动态菜单

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
│   ├── directive              # 全局指令
│   ├── filter                 # 自定义过滤器集合
│   ├── globalMethod           # 全局方法，已挂载到Vue原型上，使用this.$xx调用
│   ├── layout                 # 布局
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
│   │   │── _app               # 系统页面，如登录注册、404等
│   │   └── _common            # 公用页面
│   ├── App.vue
│   └── main.js
```
