# mock

如果不想依赖后端的话，需要将 `@/config` 中的 `useMock` 设为true，
同时在 `@/mock/controller` 里写对应的处理路由，项目中预先写了三个
基础的路由，可以让你进行 *登陆-登出* 的基础操作，可以照着代码自行扩展。

目前mock和代理不能共存，如果真想要这个功能的话，需要自己在`vue.config.js` 
中的 `devServer.proxy` 设置拦截路径。
