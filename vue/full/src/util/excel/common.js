
import request from "@/api/request"
import {isEmpty} from "@/util"
import {elError} from "@/util/message"
import {download} from "@/util/file"
import {flatTree} from "@/util/tree"

/**
 * 导出excel的抽象模板
 * 响应头为json时进行前端导出，否则直接下载文件
 *
 * @param url {string}                 搜索的请求地址
 * @param params {*}                   向后台发送请求时携带的参数
 * @param options                      前端导出时需要的配置项
 *     @param options.columns {array}  json2workbook所需参数
 *     @param options.merge {object}   json2workbook所需参数
 * @param json2workbook {function}     将json数据转换为workbook的函数，参考./exceljs.js #json2workbook
 * @param workbook2excel {function}    workbook对象转换为excel文件，参考./exceljs.js #workbook2excel
 */
export function abstractExportExcel(url, params, options, json2workbook, workbook2excel) {
    request({url, method: 'post', responseType: 'blob', data: params})
        .then(({headers, data}) => {
            const contentType = headers['content-type']
            const contentDisposition = headers['content-disposition']
            const filename = window.decodeURI(contentDisposition.split('=')[1])

            if (contentType.includes('json')) {
                const reader = new FileReader()
                reader.onload = () => {
                    const response = JSON.parse(reader.result)
                    const {columns, merge} = options
                    const workbook = json2workbook(response.data, columns, merge)
                    workbook2excel(workbook, filename)
                }
                reader.readAsText(data)
            }
            else download(data, filename)
        })
        .catch(e => elError(e))
}

/**
 * 合并excel
 * 此方法会修改原始json数据（重写序号）
 *
 * @param props {array}         需要合并的字段名数组，用于映射json
 * @param data {array}          json数组
 * @param primaryKey {string}   该行json的唯一标识字段名
 * @param orderKey {string}     该行json的序号字段名
 * @param ignoreRows {number}   忽略的行数，默认为1（忽略表头）
 * @return {array}              包含合并信息的数组，形如['A1:A10',...]
 */
export function mergeExcel(props, data, primaryKey, orderKey, ignoreRows = 1) {
    const result = [], merge = [], temp = []

    props.forEach((prop, index) => {
        merge[index] = prop ? [1] : undefined
        temp[index] = prop ? 1 : undefined
    })

    //序号从1开始
    let indexAfterMerge = data[0][orderKey] = 1

    function compare(currentRow, lastRow, nextRow, colIndex) {
        const attr = props[colIndex]

        //若与上一行的primaryKey相同且属性相同，则temp对应项+1
        if (currentRow[primaryKey] === lastRow[primaryKey]
            && currentRow[attr] === lastRow[attr]) {
            temp[colIndex]++
            //最后一行特殊处理
            if (!nextRow) return merge[colIndex].push(temp[colIndex])

            //若与下一行相同
            if (currentRow[primaryKey] === nextRow[primaryKey]
                && currentRow[attr] === nextRow[attr]) {
                merge[colIndex].push(1)
            }
            //否则存入
            else merge[colIndex].push(temp[colIndex])
        }
        //否则清零
        else {
            temp[colIndex] = 1
            merge[colIndex].push(1)
        }
    }

    for (let i = 1; i < data.length; i++) {
        const currentRow = data[i]
        const lastRow = data[i - 1]
        const nextRow = data[i + 1]

        //重写序号
        if (currentRow[primaryKey] === lastRow[primaryKey]) {
            currentRow[orderKey] = lastRow[orderKey]
        }
        else currentRow[orderKey] = ++indexAfterMerge
        if (nextRow && nextRow[primaryKey] === currentRow[primaryKey]) {
            nextRow[orderKey] = currentRow[orderKey]
        }

        props.forEach((prop, colIndex) => !isEmpty(prop) && compare(currentRow, lastRow, nextRow, colIndex))
    }

    function mergeResultConstructor(arr, colIndex) {
        const colHeader = number2excelColumnHeader(colIndex)
        const startRows = ignoreRows + 1

        arr.forEach((rowspan, rowIndex) => {
            if (rowspan > 1) {
                const start = colHeader + (startRows + rowIndex - (rowspan - 1))
                const end = colHeader + (startRows + rowIndex)
                result.push(`${start}:${end}`)
            }
        })
    }

    merge.forEach((arr, index) => arr && mergeResultConstructor(arr, index))

    return result
}

/**
 * 生成表头的二维数组以及合并结果
 * 参考element的table-header
 *
 * @param columns {array}      列配置
 * @param separator {string}   分隔符
 * @return {{header: [], mergeCells: []}}
 */
export function generateHeader(columns, separator = '-') {
    const tree = []
    let maxDepth = 1

    for (const col of columns) {
        let header = col.header

        if (typeof header === 'string') {
            header = header.split(separator)
        }

        let arr = tree, depth = header.length

        if (depth > maxDepth) maxDepth = depth

        for (let i = 0; i < depth; i++) {
            const value = header[i]

            let obj = arr.find(item => item.value === value)

            if (!obj) {
                obj = {value, depth: i + 1}
                if (i !== depth - 1) obj.children = []

                arr.push(obj)
            }

            arr = obj.children
        }
    }

    const traverse = column => {
        if (column.children) {
            let colSpan = 0
            column.children.forEach(subColumn => {
                traverse(subColumn)
                colSpan += subColumn.colSpan
            })
            column.rowSpan = 1
            column.colSpan = colSpan
        }
        else {
            column.rowSpan = maxDepth - column.depth + 1
            column.colSpan = 1
        }
    }

    tree.forEach(node => traverse(node))

    const header = []
    for (let i = 0; i < maxDepth; i++) header.push([])

    const mergeCells = []

    let lastDepth = 0, colIndex = 0 //当前是第几列，从0开始

    for (const node of flatTree(tree)) {
        //判断是否是下一列，节点深度从1开始
        if (node.depth <= lastDepth) colIndex++
        lastDepth = node.depth

        //填充表头
        header[node.depth - 1][colIndex] = node.value

        //获取合并结果，要么rowSpan大于1要么colSpan大于1，两者不可能同时大于1
        const startColHeader = number2excelColumnHeader(colIndex)
        const startCell = startColHeader + node.depth
        if (node.rowSpan > 1) {
            const endCell = startColHeader + (node.depth + node.rowSpan - 1)
            mergeCells.push(`${startCell}:${endCell}`)
        }
        if (node.colSpan > 1) {
            const endCell = number2excelColumnHeader(colIndex + node.colSpan - 1) + node.depth
            mergeCells.push(`${startCell}:${endCell}`)
        }
    }

    return {header, mergeCells}
}

/**
 * 生成二维数组，例如json为[{id:1,name:'x'}]，结果为[[1,'x']]
 *
 * @param data {array}      json数组
 * @param propMap {object}  表头和字段名的映射表，propMap[字段名]=对应的表头的数组下标
 * @return {array}          基数组为excel中每一行的二维数组
 */
export function jsonArray2rowArray(data, propMap) {
    return data.map(i => Object.keys(i)
        .reduce((arr, key) => {
            arr[propMap[key]] = i[key]
            return arr
        }, []))
}

/**
 * 数字转excel列名
 * 1 -> A  27 -> AB
 *
 * @param n {number}
 * @return {string}
 */
export function number2excelColumnHeader(n) {
    let s = ""
    while (n >= 0) {
        s = String.fromCharCode(n % 26 + 65) + s
        n = Math.floor(n / 26) - 1
    }
    return s
}
