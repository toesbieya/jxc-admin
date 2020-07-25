# 树选择

基于 `<el-select>` 和 `<el-tree>`，实现单选和多选。

<div style="display: flex;justify-content: space-between">
    <img :src="$withBase('/tree-select_single.png')">
    <img :src="$withBase('/tree-select_multiple.png')" style="margin-left: 20px">
</div>


### 引入：

`@/components/TreeSelect`

### 使用：
```html
<tree-select v-model="value" :data="data"/>
```

### TreeSelect Attributes：

| 参数                | 说明                                    | 类型       | 默认  |
| :-----------------: | :-------------------------------------: | :--------: | :---: |
| value / v-model     | 绑定值，多选时需要是array类型           | -          | -     | 
| data                | 等同于`el-tree`的`data `                | -          | -     | 
| multiple            | 是否多选                                | `boolean`  | -     | 
| disabled            | 等同于`el-select`                       | `boolean`  | -     | 
| size                | 等同于`el-select`                       | `string`   | -     | 
| clearable           | 等同于`el-select`                       | `boolean`  | true  | 
| placeholder         | 等同于`el-select`                       | `string`   | -     | 
| filterable          | 是否显示用于筛选的输入框                | `boolean`  | -     | 
| filter-method       | 等同于`el-tree`的`filter-node-method`   | `function` | -     | 
| node-key            | 等同于`el-tree`                         | `string`   | 'id'  | 
| props               | 等同于`el-tree`                         | `object`   | -     | 
| accordion           | 等同于`el-tree`                         | `boolean`  | true  | 
| popperAppendToBody  | 等同于`el-select`                       | `boolean`  | -     | 
| automaticDropdown   | 等同于`el-select`                       | `boolean`  | -     | 

### TreeSelect Events：

| 事件名称 | 说明              |
| :------: | :---------------: |
| input    | 等同于`el-select` |
| change   | 等同于`el-select` |


::: tip
使用多选时，如果value数组中有父节点但无子节点，会出现看上去父子全选中，但实际只选了父节点的情况
:::
