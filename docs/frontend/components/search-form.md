# Search Form

一个自适应的表单容器，根据父元素的内宽来确定一行渲染多少个表单控件，同时确定是否需要折叠。

<img :src="$withBase('/search-form_1.png')">
<img :src="$withBase('/search-form_2.png')">

### 引入：

`@/components/SearchForm`，`@/components/SearchFormItem`

### 使用：
```html
<search-form>
    <search-form-item label="单 号：">
        <el-input/>
    </search-form-item>
    <search-form-item label="采购订单：">
        <el-input/>
    </search-form-item>
    <search-form-item label="创建人：">
        <el-input/>
    </search-form-item>
    <search-form-item label="创建时间：">
        <el-date-picker/>
    </search-form-item>
    <search-form-item label="审核人：">
        <el-input/>
    </search-form-item>
    <search-form-item label="审核时间：">
        <el-date-picker/>
    </search-form-item>
    <search-form-item label="状 态：">
        <el-select/>
    </search-form-item>
</search-form>
```

### SearchForm Attributes：

| 参数                | 说明                                 | 类型     | 默认      |
| :-----------------: | :----------------------------------: | :------: | :-------: |
| label-width         | 等同于`el-form`的`label-width`       | `string` | `'120px'` |
| xs                  | 父元素内宽<768px时，每行渲染多少空间 | `number` | `1`       | 
| sm                  | >=768px                              | `number` | `2`       | 
| md                  | >=998px                              | `number` | `3`       | 
| lg                  | >=1200px                             | `number` | `4`       | 

### SearchForm Slots：

| 名称     | 说明                                                                    |
| :------: | :---------------------------------------------------------------------: |
| collapse | 折叠按钮，参数为 `{collapse:展开为true否则false,handle:展开折叠的方法}` |

### SearchFormItem Attributes：

| 参数  | 说明                          | 类型     | 默认      |
| :---: | :---------------------------: | :------: | :-------: |
| label | 等同于`el-form-item`的`label` | `string` | -         |
