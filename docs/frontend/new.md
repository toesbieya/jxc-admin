# 新增页面

1. 把数据请求api创建出来，建议在 `@/api`下根据模块区分。

2. 创建路由页面，建议在 `@/views` 下根据模块区分。

3. 如果页面过大过复杂，建议按功能拆分，放在当前页面的文件夹里。

4. 独有的样式同样放在当前页面的文件夹里，能公用的提取到 `@/assets/styles` 里。

5. 创建路由，根据是否需要鉴权，在 `@/router/authority/modules` 或 `@/router/constant/modules` 下创建，这两个文件夹下的js会被自动导出。

6. 如果需要创建vuex模块，请在 `@/store/modules` 下创建，同样的，该文件夹下的js会被自动导出。

7. 如果页面之间存在公共组件，建议抽取出来，根据业务耦合度拆分至 `@/components` 或 `@/bizComponents`
