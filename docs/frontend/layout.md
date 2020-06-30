# 布局

此布局是在vue-element-admin的基础上改造而来。

实际上这个布局算是二级路由，一级路由是App.vue，这就是为什么经常会看到路由会这么写的原因：
```json
{
    "path": "/",
    "component": Layout,
    "children": [
        {
            "path": "index",
            "name": "index",
            "component": Page,
            "meta": {"title": "首页"}
        }
    ]
}
```
`Layout` 填充到 `App.vue` 里的 `<router-view>`，而真正的路由页面 `@/views/index` 则填充到 `Layout` 里的 `<router-view>`。

项目中没有用到 `Layout` 的页面只有 `@/router/constant/modules/app.js` 里的 `login`、`403`、`404` 这三个。

另外可以发现 `App.vue` 使用了 `<keep-alive>`，这是为了从 `403`、`404` 返回时能保存之前打开的标签页。

## 多页签

增加了平滑滚动功能，增加了快捷键功能：`ctrl + → 下一个页签`、`ctrl + ← 上一个页签`

::: warning 注意 
多页签在 启用 / 停用 之间切换时，之前缓存的页签实例会清空，也就是说再次打开时会是新的页面
:::

## 侧边栏

重复点击菜单可刷新路由，折叠时可以选择是否在折叠菜单上显示父级。

侧边栏的滚动分两种情况，一种是整体的滚动，这是用 `<el-scrollbar>` 做的，不过缺点就是移动端体验不好。
另一种是弹出菜单的滚动，这是用 `css` 做的，不用 `<el-scrollbar>` 是因为没办法避免bug，具体原因可以去看element-ui的 `submenu #handleMouseleave`。

## 全局页脚

只有使用了 `Layout` 组件的路由页面才会有这个页脚。
这里的实现比较特别，由于路由页面被 `<el-scrollbar>` 包裹，需要将页脚的dom插入到 `<el-scrollbar>` 的dom中，所以使用了这种方法：
```javascript
const FooterConstructor = Vue.extend(Footer)
const footerInstance = new FooterConstructor().$mount()
this.$refs.scrollbar.$refs.wrap.appendChild(footerInstance.$el)
```
至于为什么不用函数式组件，是因为我还不知道怎么样拿到一个函数式组件的dom


