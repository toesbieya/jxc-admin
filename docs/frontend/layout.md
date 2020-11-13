# 布局

<img :src="$withBase('/布局.png')">

布局也是一个组件，比较重型而已，文件目录：`@/layout/index.vue`。

此布局是在vue-element-admin的基础上改造而来，并且模仿and design pro实现了三种导航模式。

实际上这个布局算是二级路由，一级路由是 `App.vue`，这就是为什么经常会看到路由会这么写的原因：
```json
{
    "path": "/",
    "component": Layout,
    "children": [
        {
            "path": "index",
            "name": "index",
            "component": Page
        }
    ]
}
```
`Layout`（指`@/layout/index.vue`） 填充到 `App.vue` 里的 `<router-view>`，
而真正的路由页面 `Page` 则填充到 `Layout` 里的 `<router-view>`。
当然，如果 `Layout` 中**没有写** `<router-view>`，那么只会显示布局组件。

项目中没有用到 `Layout` 的页面只有 `@/router/define.js` 里的 `login`、`403`、`404`、`500`。

此外，在写 `Layout` 这个重型组件时，已经尽可能地进行独立化（拥有作为独立组件的可能性）。
目前 `Layout` 的数据控制基本由 `Vue.observable` 实现（文件都在 `@/layout/store` 下），
仅有*未读消息数*、*用户信息*需要使用 `vuex`。

## 侧边栏

文件目录：`@/layout/component/Aside/index.vue`。
扩展了抽屉模式、自动隐藏这两个功能。

侧边栏**不会**在以下情形渲染：
- PC端下选择了顶部导航模式
- 没有需要显示的菜单（仅不渲染dom）

### 侧边栏LOGO

文件目录：`@/layout/component/Aside/Logo.vue`。
想更改logo地址的请修改 `@/config/index.js` 中的 `sidebarLogoUrl`，或者自己重写（需要注意侧边栏的折叠情况）

### 侧边栏菜单

侧边栏菜单借助了 `@/component/menu/NavMenu/item.vue` 来渲染菜单项，具体配置请查看[路由配置](./router.html)。

侧边栏菜单扩展了以下功能：
- 点击已激活的菜单可刷新路由
- 折叠时可显示父级菜单
- 菜单激活时自动滚动到可视区域

## 头部导航栏

文件目录：`@/layout/component/Header/component/Navbar/index.vue`。

### 汉堡包

文件目录：`@/layout/component/Header/component/Navbar/Hamburger.vue`。

PC端的行为是切换侧边栏的折叠状态，移动端是切换侧边栏在抽屉模式下的显示状态。
当侧边栏无需渲染时，汉堡包同样不会渲染。

### 导航菜单

文件目录：`@/layout/component/Header/component/Navbar/HeadMenu.vue`。

头部导航菜单基本可以视为垂直版的侧边栏菜单，也具有*点击已激活的菜单可刷新路由*的功能，此外还扩展了以下功能：
- 仿ant design的响应式（目前还存在组件未更新的问题，初步判断是element-ui的问题）

头部导航菜单**不会**在以下情形渲染：
- 是移动端
- PC端下选择了侧边栏导航模式

### 设置面板

<img :src="$withBase('/布局-设置面板.png')">

文件目录：`@/layout/component/Header/component/Navbar/SettingDrawer.vue`。

设置面板的数据与`Layout`的数据是相互独立的，这么做是为了`Layout`的独立性。
在设置面板中修改数据后，会将配置写入localStorage。
在面板mounted时，会将localStorage中的配置数据同步到`Layout`的store中

## 头部多页签

文件目录：`@/layout/component/Header/component/Tagsview/index.vue`。

在原版的基础上扩展了以下功能：
- 平滑滚动
- 快捷键响应：`ctrl + → 下一个页签`、`ctrl + ← 上一个页签`

::: warning 注意 
多页签在 启用 / 停用 之间切换时，之前缓存的页签实例会清空，也就是说再次打开时会是新的页面
:::

## 路由页面

文件目录：`@/layout/component/Page/index.vue`。

### 页头

文件目录：`@/layout/component/Page/PageHeader.vue`。

将原先在头部导航栏的面包屑挪到了这里（参考[dcat-admin](https://github.com/jqhph/dcat-admin)）。
这里说明一下面包屑的实现，是直接用$route.matched做的，没有考虑其他特殊情况。

不想显示页头的可以将路由配置项的`meta.pageHeader`设为`false`。

### 页脚

文件目录：`@/layout/component/Page/component/PageFooter.vue`。

只是一个静态dom，想咋改就咋改。

目前页脚全局显示，没有提供配置来隐藏，不想显示的直接删除即可。


