import {isEmpty} from "@/util"
import {download} from "@/util/file"
import {abstractExportExcel, jsonArray2rowArray, mergeExcel, generateHeaders, number2excelColumnHeader} from "./common"

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

export function exportExcel(url, searchForm, options) {
    abstractExportExcel(url, searchForm, options, json2workbook, exportExcelByJs)
}

//纯前端导出excel
export function exportExcelByJs(workbook, filename) {
    return workbook.xlsx.writeBuffer()
        .then(buffer => download(new Blob([buffer], {type: "application/octet-stream"}), filename))
}

/**
 * @desc 通过json数组生成workbook对象
 * @param data        json数组
 * @param columns     列配置
 * @param merge       合并配置项
 * @returns workbook
 */
export function json2workbook(data, columns, merge) {
    const workbook = new window.ExcelJS.Workbook()
    const sheet = workbook.addWorksheet('Sheet1')
    const columnSettings = []
    const propMap = {}   //表头和字段名的映射表，propMap[字段名]=对应的表头的数组下标
    const needMerge = [] //合并行配置，[i]=字段名

    columns.forEach((col, i) => {
        if (!isEmpty(col.prop)) propMap[col.prop] = i
        if (col.merge) needMerge[i] = col.prop
        columnSettings[i] = {width: col.width || 20, style: defaultCellStyle}
    })

    sheet.columns = columnSettings

    const {headers, headerMerge} = generateHeaders(columns)

    if (headers.length > 0) {
        sheet.addRows(headers)
        headers.forEach((rows, rowIndex) => {
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

    let bodyMerge = []

    if (merge && needMerge.length > 0) {
        const {primaryKey, orderKey} = merge
        bodyMerge = mergeExcel(needMerge, data, primaryKey, orderKey, headers.length)
    }

    const result = jsonArray2rowArray(data, propMap)

    sheet.addRows(result)

    //exceljs的合并行必须在数据添加后进行
    headerMerge.forEach(i => sheet.mergeCells(i))
    bodyMerge.forEach(i => sheet.mergeCells(i))

    return workbook
}
