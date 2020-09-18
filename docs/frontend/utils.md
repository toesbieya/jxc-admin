# 常用工具类

## 日期格式化
```js
import {timeFormat} from '@/utils'

// 以 yyyy-MM-dd HH:mm:ss 格式显示的当前时间
timeFormat()

// 06-29
timeFormat('MM-dd',new Date('2020/06/29'))
```

## 数学运算

基于 `decimal.js` 封装了加减乘除运算方法。

```js
import {plus,sub,mul,div} from '@/utils/math'

//1.05 * 10.8 / 0.6 /0.1 + 0.1 + 0.5 - 0.2 - 0.3
sub(plus(div(mul(1.05,10.8),0.6,0.1),0.1,0.5),0.2,0.3)
```

## 列表转树

```js
import {createTree} from '@/utils/tree'

const arr1 = [{id: 1, pid: 0, name: 'a'}, {id: 2, pid: 1, name: 'b'}, {id: 3, pid:2, name: 'c'}]
const tree1 = createTree(arr1)

const arr2 = [{xid: 1, ypid: 0, name: 'a'}, {xid: 2, ypid: 1, name: 'b'}, {xid: 3, ypid: 2, name:' c'}]
const tree2 = createTree(arr2, 0, 'xid', 'ypid')
```

## 纯前端导出excel

依赖exceljs，项目中使用script引入。
```js
import {json2workbook, workbook2excel} from '@/utils/excel'

const column = [
    {header: ['合并表头演示', '序号'], prop: 'no', merge: true},
    {header: '合并表头演示-名称', prop: 'name', merge: true},
    {header: '日期', prop: 'date'}
]

const arr = [
    {no: 1,name: '老王',date: '20190201'}
]

const workbook = json2workbook(arr, column)

workbook2excel(workbook, '测试导出.xlsx')
```

### json2workbook(data, columns, merge) : workbook

- `data` : `array`，json数组。
- `columns` : `array`，列配置项数组，列配置详细如下：

| 参数   | 说明                                                          | 类型           | 默认 |
| :----: | :-----------------------------------------------------------: | :------------: | :--: |
| header | 表头名称，通过使用'-'分隔符或者传入array来实现多级表头        | `string|array` | -    |
| prop   | 表头对应的 json 字段名称，如果为空，则该字段不会写入excel表格 | `string`       | -    |
| merge  | 是否需要合并，true的话会从上至下合并值相同的相邻单元格        | `boolean`      | -    |
| width  | 单元格宽度                                                    | `number`       | 20   |

- `merge` : `object`，合并配置项，详细如下：

| 参数       | 说明                           | 类型     | 默认 |
| :--------: | :----------------------------: | :------: | :--: |
| primaryKey | 每行json数据的唯一标识字段名称 | `string` | -    |
| orderKey   | 每行json数据的序号字段名称     | `string` | -    |

### workbook2excel(workbook, filename)

- `workbook`，exceljs的workbook对象
- `filename` : `string`，导出的文件名称，需要带上.xlsx后缀
