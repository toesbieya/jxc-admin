# 二次封装的element-ui组件

这些组件位于 `@/component/abstract` 下，均为函数式组件，无特殊声明时与原始的 `element-ui` 组件没有区别。

## Dialog

位于 `@/component/abstract/Dialog/index.vue`。

可拖拽，超出滚动（使用 `el-scrollbar`），使用 `value`（支持 `v-model`） 代替 `visible`，新增了 `loading` 属性。

## Form

位于 `@/component/abstract/Form`。

对 `el-form` 和 `el-form-item` 做了默认封装（`index.vue` 和 `item.vue`），使用 `el-row` 和 `el-col` 包裹。

用法：
```vue
<abstract-form :model="model" :rules="rules">
    <abstract-form-item label="xxx" prop="xxx">
        <el-input/>
    </abstract-form-item>
</abstract-form>
```

`abstract-form` 的其他用法与 `el-form` 没有区别，`abstract-form-item` 参数如下：

| 名称  | 说明                                   | 类型    | 默认值 |
|:-----:|:--------------------------------------:|:-------:|:------:|
| full  | 为 `true` 时，将占满一行               | Boolean | -      |
| thin  | 为 `true` 时，屏幕宽度>=1200px下占12列 | Boolean | -      |
| label | 等同于 `el-form-item` 的 `label`       | String  | -      |
| prop  | 等同于 `el-form-item` 的 `prop`        | String  | -      |

::: tip 注意
当 `abstract-form-item` 的 `full` 为 `true` 时，可以认为是 `el-form-item` 的子集
:::

## Pagination

位于 `@/component/abstract/Pagination/index.vue`。

设置了一些默认属性，与 `el-pagination` 的区别是增加了 `model` 属性，
`model` 是一个 `{total, page, pageSize}` 对象，对应 `el-pagination` 的 `total`、`current-page`、`page-size` 属性

## Table

位于 `@/component/abstract/Table/index.vue`。

给 `<el-table>` 设置了一些默认属性，设置了 `empty` 插槽（位于 `@/component/Empty`）。
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

