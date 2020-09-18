# Abstract Table

<img :src="$withBase('/abstract-table.png')">

给`<el-table>`设置了一些默认属性，设置了`empty`插槽。
```html
<el-table
    ref="table"
    current-row-key="id"
    row-key="id"
    highlight-current-row
    {...data}
    >
    {children}
    <Empty slot="empty"/>
</el-table>
```

### 引入：

`@/component/AbstractTable`。

### 使用：

照着官方文档，当成 `<el-table>` 用就行。

