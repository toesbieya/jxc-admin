# 行政区划选择器

目前分 `树型` 和 `选项卡型` 两种，由于 `树型` 不完善，所以这里只介绍 `选项卡型`，有兴趣的可以查看源码`@/component/RegionSelector/Tree`。

<img :src="$withBase('/region-selector_tab.png')">

### 引入：

`@/component/RegionSelector`

### 使用：
```html
<region-selector v-model="value"/>
```

行政区划的数据模型可以查看`public/static/json/region-pcas.json`，这是精确到街道的数据，[由此获取](https://github.com/modood/Administrative-divisions-of-China)。

### RegionSelector Attributes：

| 参数                | 说明                                    | 类型             | 默认    |
| :-----------------: | :-------------------------------------: | :--------------: | :-----: |
| type                | 选择器类型，`tab / tree`                | `string`         | `'tab'` |
| regionDataUrl       | 行政区域json的请求地址                  | `string`         | `${process.env.BASE_URL}static/json/region-pca.json` |
| value / v-model     | 绑定值                                  | `string / array` | -       | 
| readonly            | 等同于`el-select`的`disabled`           | `boolean`        | -       | 
| size                | 等同于`el-select`的`size`               | `string`         | -       | 
| maxLevel            | 可选择的最大深度，[1,4]                 | `number`         | 3       | 
| getChildrenOnSelect | 在确认选择时是否连同子级一起获取        | `boolean`        | -       | 
| limit               | 是否需要剪枝                            | `boolean`        | -       | 
| limitApi            | 获取剪枝数据的api                       | `function`       | -       | 
| separation          | 分隔号                                  | `string`         | `','`   | 

### RegionSelector Events：

| 事件名称 | 说明                       | 回调参数         |
| :------: | :------------------------: | :--------------: |
| clear    | 等同于`el-select`的`clear` | -                |
| select   | 确定选择时触发             | `(node,idArray)` |


::: tip
`value` 为 `string` 时支持被 `separation` 所连接的汉字或者数字，比如可以是 `北京市，东城区` ，也可以是 `北京市，110101`。
为 `array` 时，数组里的每一项除了支持上面的形式外，还支持 `{id:'11',...}、{name:'北京市',...}` 这两种。

使用 `v-model` 时，不管初始传入的数据是什么样的，最终都会返回 `北京市，东城区` 这样的字符串。
:::
