import {isEmpty} from "@/util"
import {download} from "@/util/file"
import {
    abstractExportExcel,
    jsonArray2rowArray,
    mergeExcel,
    generateHeader,
    number2excelColumnHeader
} from "./common"

//默认的excel单元格样式
const defaultCellStyle = {
    font: {name: '宋体'},
    alignment: {
        wrapText: true,// 自动换行
        horizontal: "center",
        vertical: "middle",
        indent: 0
    }
}

//默认的头部样式
const defaultHeaderStyle = {
    ...defaultCellStyle,
    fill: {type: 'pattern', pattern: 'solid', fgColor: {argb: "FFC0C0C0"}},
    font: {name: '宋体', size: 14, bold: true},
    border: {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}}
}

/**
 * excel导出的具体实现
 */
export function exportExcel(url, params, options) {
    abstractExportExcel(url, params, options, json2workbook, workbook2excel)
}

export function workbook2excel(workbook, filename) {
    return workbook.xlsx.writeBuffer()
        .then(buffer => download(new Blob([buffer], {type: "application/octet-stream"}), filename))
}

/**
 * 通过json数组生成workbook对象
 *
 * @param data {array}                         json数组
 * @param columns {array}                      列配置
 * @param mergeOption                          合并配置项
 *     @param mergeOption.primaryKey {string}  ./common.js #mergeExcel所需参数
 *     @param mergeOption.orderKey {string}    ./common.js #mergeExcel所需参数
 * @return workbook                            exceljs的workbook对象
 */
export function json2workbook(data, columns, mergeOption) {
    const columnStyle = [] //列设置
    const propMap = {}     //表头和字段名的映射表，propMap[字段名]=对应的表头的数组下标
    const needMerge = []   //合并行配置，[i]=字段名

    columns.forEach((col, i) => {
        if (!isEmpty(col.prop)) propMap[col.prop] = i
        if (col.merge) needMerge[i] = col.prop
        columnStyle[i] = {width: col.width || 20, style: defaultCellStyle}
    })

    const {header, mergeCells} = generateHeader(columns)

    if (mergeOption && needMerge.length > 0) {
        const {primaryKey, orderKey} = mergeOption
        mergeCells.push(...mergeExcel(needMerge, data, primaryKey, orderKey, header.length))
    }

    const body = jsonArray2rowArray(data, propMap)

    return generateWorkbook('Sheet1', columnStyle, header, body, mergeCells)
}

/**
 * 生成workbook的代码抽取
 *
 * @param sheetName {string}    第一个sheet的名称
 * @param columnStyle {array}   列样式
 * @param header {array}        头部，二维数组
 * @param body {array}          内容，二维数组
 * @param mergeCells {array}    需要合并的单元格，形如['A1:B1'...]
 * @return workbook
 */
function generateWorkbook(sheetName, columnStyle, header, body, mergeCells) {
    const workbook = new window.ExcelJS.Workbook()
    const sheet = workbook.addWorksheet(sheetName)

    if (header) {
        sheet.addRows(header)
        //设置头部单元格样式
        header.forEach((rows, rowIndex) => {
            rows.forEach((cellValue, colIndex) => {
                if (cellValue !== undefined) {
                    const cell = sheet.getCell(number2excelColumnHeader(colIndex) + (rowIndex + 1))
                    Object.keys(defaultHeaderStyle).forEach(key => {
                        cell[key] = defaultHeaderStyle[key]
                    })
                }
            })
        })
    }
    body && sheet.addRows(body)

    //exceljs的合并行必须在数据添加后进行
    mergeCells && mergeCells.forEach(i => sheet.mergeCells(i))

    return workbook
}
