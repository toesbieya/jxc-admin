# 用户导航

这是在driver.js的基础上改造来的。

### 使用：

```js
//配置导航
this.$guide.config(config)

//开始一个导航
this.$guide(steps,index[,beforeExist])

//退出导航
this.$guide.exit()
```

### $guide.config(config)

- config，所有可配置项如下：
```js
{
    stageBackground: "#ffffff",//弹框背景颜色
    doneBtnText: "完成",       //完成按钮的文字
    closeBtnText: "关闭",      //关闭按钮的文字
    nextBtnText: "下一步",     //下一步按钮的文字
    prevBtnText: "上一步",     //上一步按钮的文字
}
```

### $guide(steps,index[,beforeExist])

- `step` : `array`，导航步骤数组，所有可配置项如下：

- `index` : `number / string`，从第几个步骤开始，`number` 时从0开始，`string` 时可以为 `'first'` 或 `'last'`，默认为0。

```js
{
    ...config,
    element: string, 用于querySelector,
    content: string, 弹框内容，可以使用html,
    forceShowPrevBtn: boolean, 是否强制显示上一步按钮
    forceShowNextBtn: boolean, 是否强制显示下一步按钮
    onPrevious: function, 跳转到上一个导航步骤前触发，返回false或Promise.reject()来阻止跳转
    onNext: function, 跳转到下一个导航步骤前触发，返回false或Promise.reject()来阻止跳转
    onHighlighted: function，当前导航步骤高亮完成后触发
}
```

- `beforeExist` : `function`，退出导航前触发，回调参数是一个 `boolean`，表示当前是否是最后一个步骤，返回 `false` 或 `Promise.reject()` 来阻止退出。

### $guide.exit(force)

- `force` : `boolean`，`true` 时会强制退出，忽略所有钩子，否则会触发 `beforeExist`
