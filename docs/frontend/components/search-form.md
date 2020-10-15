# Search Form

一个自适应的搜索表单，模仿ant-design-pro，根据父元素的内宽来确定一行渲染多少个表单控件，同时确定是否需要折叠。

<img :src="$withBase('/search-form_1.png')">
<img :src="$withBase('/search-form_2.png')">

### 引入：

`@/component/SearchForm`

### 使用：
```html
<search-form v-model="searchForm" @search="search">
    <el-form-item label="label1">
        <el-input/>
    </el-form-item>
    <el-form-item label="label2">
        <el-input/>
    </el-form-item>
</search-form>
```

### SearchForm Attributes：

| 参数        | 说明                                 | 类型     | 默认      |
| :---------: | :----------------------------------: | :------: | :-------: |
| model       | 表单数据，支持v-model，用于重置表单  | `object` | -         |
| label-width | 等同于 `el-form` 的 `label-width`    | `string` | `'120px'` |
| xs          | 父元素内宽<768px时，每行渲染多少空间 | `number` | `1`       | 
| sm          | >=768px                              | `number` | `2`       | 
| md          | >=998px                              | `number` | `3`       | 
| lg          | >=1200px                             | `number` | `4`       | 

### SearchForm Events：

| 名称   | 说明                                                                    |
| :----: | :--------------------------------: |
| search | 点击查询按钮触发                   |
| reset  | 点击重置按钮触发，参数为表单初始值 |

::: tip 注意
`<search-form>` 算是对 `<el-form>` 的封装，其 `label-suffix` 属性为`':'`，
也就是说 `<search-form>` 下的所有 `<el-form-item>` 项的label后缀为`':'`
:::
