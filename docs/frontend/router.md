# 路由和菜单

项目中默认使用了前端预设的静态路由，
要使用后端返回的数据来生成动态路由和菜单的话需要将 `@/config/index.js` 中的 `route.useBackendDataAsRoute` 设为 `true`。

本项目不支持二级以上的路由，虽然路由配置可以无限级，但是实际上是二级路由的效果。
因为在处理路由数据时会将二级以上的路由缩减为其叶子节点，也就是说如果有如下路由：
```js
const routes = [
    {
        path: '/a',
        component:A,
        children: [
            {
                path: 'b',
                component:B,
                children: [
                    {
                        path: 'c',
                        component:C
                    },
                    {
                        path: 'd',
                        component:D,
                        children: [
                            {
                                path: 'e',
                                component:E,
                            }
                        ]
                    }
                ]
            }
        ]
    }
]
```
缩减后是下面的形式：
```js
const routes = [
    {
      path: '/a',
      component: A,
      children: [
        { path: 'b/c', component: C },
        { path: 'b/d/e', component: E }
      ]
    }
]
```
这样做的目的是为了避免多级路由缓存失效的问题，而且大部分情况下，三级路由（或更高）可以借助动态组件来实现同样的效果。

## 路由配置项

本项目的路由配置除了`component`以外，与`vue-router`没有区别，仅在`meta`中添加了一些用于生成路由和菜单的字段。

当使用前端定义路由时（`@/config`中`route.useBackendDataAsRoute`为`false`），`component`可以是字符串，
也可以是一个返回`Promise`的函数，比如`() => import('xxx')`，否则只能为字符串。

当`component`为字符串时，比如组件路径是`@/view/test/indexPage.vue`，
那么值可以是`test/`、`test/index`、`test/indexPage`、`test/indexPage.vue`，
具体可以看`@/router/util.js` 中的 `generateRoutes` 方法。

这里约定，配置项中只有根节点的path以'/'开头（这也是`vue-router`所要求的），并且只有根节点和叶子节点可以自定义`component`。

目前根节点的`component`固定为`Layout`。

此外，所有根节点（经过降级缩减）的`redirect`属性都将设为其第一个子节点的`fullPath`，`redirect`有值的不作处理。

路由配置会经过一些变换以转换成el-admin-layout需要的菜单，所以项目中的路由meta的数据结构为el-admin-layout中[路由meta](https://doc.toesbieya.cn/el-admin-layout/api/#%E8%B7%AF%E7%94%B1meta%E9%85%8D%E7%BD%AE) 和[菜单meta](https://doc.toesbieya.cn/el-admin-layout/api/#%E8%8F%9C%E5%8D%95%E9%85%8D%E7%BD%AE)的统合

因为一些需要，项目中的路由meta相比于上述会多一些，具体如下表：

### Route.meta Extra Attributes：

| 参数           | 说明                                                 | 类型       | 默认  |
| :------------: | :--------------------------------------------------: | :--------: | :---: |
| hidden         | 是否在菜单中隐藏                                     | `boolean`  | -     |
| noAuth         | true时路由不需要鉴权                                 | `boolean`  | -     |

::: tip 注意
路由meta上的`noCache`、`noAuth`会被子路由继承，优先使用子路由的值
:::

## 路由免登陆

在 `@/router/guardian/accessControl.js` 中有这么一句:
```js
const noLoginList = ['/login', '/register', '/403', '/404', '/500']
```
被`noLoginList`匹配的路由不需要登录（同样也不需要鉴权），直接放行。

一般来说，不需要登录即可访问的路由都不以`Layout`作为组件，并且这东东基本不需要修改。

## 路由守卫

所有路由守卫全部位于`@/router/guardian`中。

预设的路由守卫如下：
- nprogress进度条守卫
- 登陆和权限守卫

::: warning 注意
路由守卫是有先后顺序的，请查看`@/router/guardian/index.js`
:::

## 首屏加载效果

<img :src="$withBase('/路由和菜单-首屏加载效果.png')">

想体验的可以把 `network` 设为 `slow 3g` 就可以在在线实例看到效果。

原理很简单，在 `index.html` 里有这么一段：
```html
<div id="app">
    <div>
        <img>
        <p>资源加载中......</p>
    </div>
</div>
```
在 `vuejs` 初始化完成后，`app` 里的内容会被替换，从而实现加载效果。

## 骨架屏

<img :src="$withBase('/路由和菜单-骨架屏.png')">

通常情况下，路由组件都是以 `() => import(...)` 的形式异步引入，
如果想让路由在加载异步组件时显示，可以使用以下方式：
```js
function lazyLoadView(component) {
    const AsyncHandler = () => ({component, loading: Skeleton})
    return () => Promise.resolve({
        functional: true,
        render(h, {data, children}) {
            return h(AsyncHandler, data, children)
        }
    })
}

//将路由配置中的component项改为以下形式
component: lazyLoadView(import(...))
```
[这是vue-router的官方文档](https://github.com/vuejs/vue-router/pull/2140/files) ，项目中具体的使用方式可以查看 `@/router/util #generateRoutes`方法。

关于 `AsyncHandler` 的更多选项，可以查看[vue官方文档](https://cn.vuejs.org/v2/guide/component-dynamic-async.html#%E5%A4%84%E7%90%86%E5%8A%A0%E8%BD%BD%E7%8A%B6%E6%80%81)
