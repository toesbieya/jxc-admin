# 指令

## el-dialog拖拽

已全局注册，使用方式：
```js
<el-dialog v-drag-dialog/>
```

## 波纹

从[quasar](https://quasar.dev/vue-directives/material-ripple)处搬运
已全局注册，使用方式：
```js
<div v-ripple/>
```
更多细节请根据quasar文档使用

## element-ui自带

### clickOutside

未全局注册，使用方式：
```js
//引入
import ClickOutside from 'element-ui/lib/utils/clickoutside'

//组件中注册
directives: {ClickOutside}

//使用
<div v-click-outside="..."/>
```
