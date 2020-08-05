# 介绍

这是在[vue-element-admin](https://github.com/PanJiaChen/vue-element-admin) 的基础上改造而来的。

## 功能

```
- 首屏加载动画

- 登录 \ 注册
  - 集成多种canvas背景动画
  - 仿极验滑动验证

- 布局功能
  - 异步路由组件loading骨架屏
  - 动态侧边栏（支持多级路由嵌套）
  - 动态面包屑
  - 多页签（支持详情页缓存）
  - 支持移动端
  - 导航栏、侧边栏可隐藏
  - 侧边栏折叠时可显示上级

- Excel导出，支持多级表头、合并行

- 错误页面
  - 403
  - 404

- 组件
  - 用户导航
  - 行政区划选择器，支持树形和选项卡型
  - 封装的上传组件，整合七牛云，支持多种格式的文件预览
  - 封装了antd风格的dialog，可拖拽、超出可滚动
  - 手写签名

- element-ui增强
  - message支持分组、进度条功能
  - select重写，分别使用了popperjs的v1和v2版本
```

## 目录结构
```
├── element-ui-personal        # element-ui增强
├── mock                       # 项目mock
├── public                     # 静态资源文件夹
├── src
│   ├── api                    # 数据请求集合
│   ├── asset
│   │   │── icon               # 项目所有svg icons
│   │   └── style              # 样式集合
│   ├── component              # 公用组件
│   ├── config
│   │   │── index.js           # 基础配置项
│   │   └── request.js         # axios封装
│   ├── directive              # 自定义指令集合
│   ├── filter                 # 自定义过滤器集合
│   ├── globalMethod           # 全局方法，使用this.$xx调用
│   ├── layout                 # 布局文件夹，包含了导航栏、侧边栏、多页签、全局页脚
│   ├── mixin                  # 公用混入
│   ├── plugin                 # 一些插件，均使用import()来动态引入
│   ├── router
│   │   │── authority          # 需要鉴权的路由集合
│   │   │── constant           # 不需要鉴权的路由集合
│   │   │── index.js
│   │   └── util.js
│   ├── store                  # vuex配置
│   ├── utils                  # 工具类
│   ├── view                   # 路由页面集合
│   ├── App.vue
│   └── main.js
```
