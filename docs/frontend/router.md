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


### Route.meta Attributes：

| 参数           | 说明                                                 | 类型       | 默认  |
| :------------: | :--------------------------------------------------: | :--------: | :---: |
| title          | 路由在侧边栏、面包屑、页签的名称                     | `string`   | -     |
| dynamicTitle   | 路由在面包屑、页签中的动态名称，参数为(to,from)      | `function` | -     |
| hidden         | 是否在菜单中隐藏                                     | `boolean`  | -     |
| alwaysShow     | 是否总是把只有一个子级的菜单以嵌套模式展示           | `boolean`  | -     | 
| sort           | 侧边栏的排序值，值越小越靠前                         | `number`   | 10000 | 
| icon           | 图标名，`<v-icon>`的icon属性                         | `string`   | -     |
| affix          | 是否在多页签中固定显示                               | `boolean`  | -     |
| noCache        | true时不缓存页面                                     | `boolean`  | -     |
| activeMenu     | 当前激活菜单的index，因为激活菜单可能并非当前路由    | `string`   | -     |
| noAuth         | true时路由不需要鉴权                                 | `boolean`  | -     |
| iframe         | 需要打开的iframe的地址                               | `string`   | -     |
| usePathKey     | 是否使用$route.path作为组件缓存的key                 | `boolean`  | -     |
| useFullPathKey | 是否使用$route.fullPath作为组件缓存的key             | `boolean`  | -     |
| commonModule   | 共用组件的唯一标识，用于判断路由是否复用组件         | `any`      | -     |

::: tip 注意
路由meta上的noAuth、noCache会被子路由继承，优先使用子路由的值
:::

## 页面缓存

先总结一下路由页面被缓存的条件：
- 启用了多页签（`@/layout/store/tagsView.js`中的`enabled`为`true`）
- `(route.name || route.meta.usePathKey || route.meta.useFullPathKey) && !route.meta.noCache && !route.meta.iframe`

通常情况路由想做缓存都是通过 `<keep-alive>`，但是如果是像 `/edit/1`、`/edit/2` 这样的共用路由页面的详情页的话，
`<keep-alive>` 就满足不了需求，因为它是通过组件的 `name` 来区分组件的。
所以项目中仿照 `<keep-alive>` 写了 `<keep-router-view-alive>`，可以根据当前路由的路径来区分组件。

除此之外还有个问题，在共用了同一个组件的路由之间跳转的话，组件会被复用，假如有以下路由：
```js
const component={template:`<input v-model="data">`, data: ()=> ({data:1})}

const routes = [
  { path: '/foo', component },
  { path: '/bar', component }
]
```
先访问 `/foo`，然后将输入框的值改为100，再访问 `/bar` ，会发现输入框的值还是100。
所以在 `router.beforeEach` 中做了判断，如果跳转前后的路由的 `meta.commonModule` 相同的话，
那么借助 `/redirect` 跳转一次，从而避免组件复用的问题

::: warning 注意
如果使用`name`作为组件缓存的标识，那么缓存的页面的`name`必须和路由的`name`一致
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
- 防止路由复用组件的守卫
- 修改页面标题信息的守卫
- 登陆和权限守卫
- 控制iframe显隐的守卫

::: warning 注意
路由守卫是有先后顺序的，请查看`@/router/guardian/index.js`
:::

## 首屏加载效果

<img :src="$withBase('/路由和菜单-首屏加载效果.png')">

想体验的可以把 `network` 设为 `slow 3g` 就可以在在线实例看到效果。

原理很简单，在 `index.html` 里有这么一段：
```html
<div id="app">
    <div id="loader-wrapper">
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

## 过渡动画

正常 `<router-view>` 使用过渡动画的方法应该是这样的：
```html
<transition>
    <keep-alive>
        <router-view>
    </keep-alive>
</transition>
```

但是由于项目中使用了自定义的 `<keep-router-view-alive>`，所以就变成这样的：
```html
<keep-router-view-alive>
    <transition>
        <router-view>
    </transition>
</keep-router-view-alive>
```

具体原因是 `<transition>` 中的代码硬编码了 `<keep-alive>`，导致如果按照往常写法的话，过渡动画不会生效。

项目中是根据页签的前后位置来决定路由动画的，比如从前面的页签跳转到后面的，那么动画是从左滑出，反之是从右滑出，源码位于`@/layout/mixin/decideRouterTransition.js`。

::: warning 注意
只有在启用了多页签的时候才会有上面的效果，否则默认是 `@/config/index.js` 中的 `route.animate.default` 动画。
:::

## iframe

只需要按如下配置即可打开一个iframe：
```json
{
    "path":"/baidu",
    "meta":{
        "iframe":"https://www.baidu.com"
    }
}
```

当向上面的路由跳转时，会隐藏路由页面，展示 `<iframe>` 标签，反之则隐藏。
另外，相同地址的iframe不会重复打开，想要刷新需使用 `/redirect` 跳转。

iframe页面的缓存与普通页面相同，都是使用 `meta.noCache` 控制。

## 外链

也可以在路由中配置一个外链，只要在 `path` 中填写了以 `'http'` 开头的路径，当你点击该路由菜单时就会新开这个页面。

外链**只**存在与菜单中，**不会**被 `vue-router` 识别。

例如：
```json
{
  "path": "https://www.baidu.com",
  "meta": {
      "title": "百度"    
  }
}
```

::: tip 注意
由于在`<el-menu>`中，菜单（`<el-menu-item>`）只要被点击就会高亮，所以如果不希望外链菜单被高亮，
那么需要在点击后通过调用`<el-menu>`的`updateActiveIndex`方法来重新设置高亮菜单。
:::
