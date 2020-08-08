# 路由

## 路由配置项

路由配置项不光参与路由的生成，也参与侧边栏的生成。

### Route.meta Attributes：

| 参数           | 说明                                                 | 类型       | 默认                    |
| :------------: | :--------------------------------------------------: | :--------: | :---------------------: |
| title          | 路由在侧边栏、面包屑、页签、搜索结果中的显示名称     | `string`   | -                       |
| dynamicTitle   | 路由在面包屑、页签中的动态名称，参数为(to,from)      | `function` | -                       |
| hidden         | 是否在侧边栏中显示                                   | `boolean`  | -                       |
| alwaysShow     | 是否总是把只有一个子级的菜单以嵌套模式在侧边栏中展示 | `boolean`  | -                       | 
| sort           | 侧边栏的排序值，值越小越靠前                         | `number`   | 10000                   | 
| icon           | 图标名，支持svg-icon、el-icon                        | `string`   | -                       |
| affix          | 是否在多页签中固定显示                               | `boolean`  | -                       |
| noCache        | true时缓存页面                                       | `boolean`  | -                       |
| activeMenu     | 侧边栏当前激活菜单的index                            | `string`   | -                       |
| noAuth         | true时路由不需要鉴权                                 | `boolean`  | router/constant下为true |
| iframe         | 需要打开的iframe的地址                               | `string`   | -                       |
| usePathKey     | 是否使用$route.path作为组件缓存的key                 | `boolean`  | -                       |
| useFullPathKey | 是否使用$route.fullPath作为组件缓存的key             | `boolean`  | -                       |
| commonModule   | 共用组件的唯一标识                                   | `any`      | -                       |

::: tip 注意
路由meta上的noAuth、noCache会被子路由继承，优先使用子路由的值
:::

## 页面缓存

先总结一下路由页面被缓存的条件：
- 启用了多页签
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
所以在 `router.beforeEach` 中做了判断，如果跳转前后的路由的 `meta.commonModule` 相同的话，那么借助 `/redirect` 跳转一次，从而避免组件复用的问题

::: warning 注意
如果使用 `name`，需要缓存的页面的 `name` 必须和路由的 `name` 一致
:::

## 路由白名单

在 `@/router/index.js` 中有这么一句:
```js
const whiteList = transformWhiteList(['/login', '/register', '/404', '/403'])
```
这个 `whiteList` 就是白名单，里面的值会被转为正则表达式，详细请查看[path-to-regexp文档](https://github.com/pillarjs/path-to-regexp)。
被`whiteList` 匹配的路由不需要验证，直接放行。

## 首屏加载效果

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

通常情况下，路由组件都是以 `import(...)` 的形式异步引入，
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

关于 `AsyncHandler` 的更多选项，可以查看[官方文档](https://cn.vuejs.org/v2/guide/components-dynamic-async.html#%E5%A4%84%E7%90%86%E5%8A%A0%E8%BD%BD%E7%8A%B6%E6%80%81)

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
<keep-router-view-alive :include="cachedViews">
    <transition :name="transitionName" mode="out-in">
        <router-view>
    </transition>
</keep-router-view-alive>
```

具体原因是 `<transition>` 中的代码硬编码了 `<keep-alive>`，导致如果按照往常写法的话，过渡动画不会生效。

项目中是根据页签的前后位置来决定路由动画的，比如从前面的页签跳转到后面的，那么动画是从左滑出，反之是从右滑出。

::: warning 注意
只有在启用了多页签的时候才会有上面的效果，否则默认是 `el-fade-in-linear` 动画。
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

## 外链

也可以在路由中配置一个外链，只要在 path 中填写了合法的 url 路径，当你点击该路由菜单时就会新开这个页面。

例如：
```json
{
  "path": "https://www.baidu.com",
  "meta": {
      "title": "百度"    
  }
}
```
