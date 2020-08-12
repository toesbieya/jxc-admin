# 拼图滑块验证

<img :src="$withBase('/puzzle-verify.png')">

### 使用：

```js
this.$verify().then(...).catch(...)
```

返回一个 `promise`，如果验证失败会 `reject`，否则 `resolve`
