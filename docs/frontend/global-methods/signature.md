# 手写签名

### 使用：

```js
//开始签名
this.$signature({image, lineWidth, lineColor, onConfirm)

//退出签名
this.$signature.close()
```

### $signature({image, lineWidth, lineColor, onConfirm)

- `image` : `string`，需要回显的签名图片的src。

- `lineWidth` : `number`，线的宽度，默认 `6`

- `lineColor` : `string`，线的颜色，默认 `'#000000'`

- `onConfirm` : `function`，签名完毕后触发，回调参数为签名图片的base64
