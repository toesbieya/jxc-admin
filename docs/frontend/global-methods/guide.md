# 用户导航

这是在driver.js的基础上改造来的。

### 使用：

- 在 `@/mixins/guide/data` 下创建导航步骤，例如 `test.js`。

- 在需要导航的路由页面里引入 `@/mixins/guide`，并混入 `mixins: [guide.test]`

```js
//配置导航
this.$guide.config(config)

//开始一个导航
this.$guide(start,steps,[,beforeExist])

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

### $guide(start,steps,[,beforeExist])

- `start` : `number / string`，从第几个步骤开始，`number` 型从0开始，`string` 型可以为 `'first'` 或 `'last'`。

- `step` : `array`，导航步骤数组，所有可配置项如下：
```js
{
    ...config,
    element: string, 用于querySelector,
    content: string, 弹框内容，可以使用html,
    forceShowPrevBtn: boolean, 是否强制显示上一步按钮
    forceShowNextBtn: boolean, 是否强制显示下一步按钮
    onPrevious: function, 跳转到上一个导航步骤前触发，返回false或Promise.reject()来阻止跳转
    onNext: function, 跳转到下一个导航步骤前触发，返回false或Promise.reject()来阻止跳转
}
```

- `beforeExist` : `function`，退出导航前触发，回调参数是一个 `boolean`，表示当前是否是最后一个步骤，返回 `false` 或 `Promise.reject()` 来阻止退出。

### $guide.exit()

退出导航，会触发 `beforeExist` 钩子
