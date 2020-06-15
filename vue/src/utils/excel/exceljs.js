import {isEmpty} from "@/utils"
import {download} from "@/utils/file"
import {abstractExportExcel, jsonArray2rowArray, mergeExcel, number2excelColumnHeader} from "./common"

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
 * @param column      列配置
 * @param data        json数组
 * @param mergeOption 合并配置项
 * @param headerRows  表头占的行数，为0时表头不写入表格
 * @returns
 */
export function json2workbook(column, data, mergeOption, headerRows = 1) {
    const workbook = new window.ExcelJS.Workbook()
    const sheet = workbook.addWorksheet('Sheet1')
    const colWidth = []
    const header = []    //表格头部
    const propMap = {}   //表头和字段名的映射表，propMap[字段名]=对应的表头的数组下标
    const needMerge = [] //合并行配置，[i]=字段名

    column.forEach((item, i) => {
        if (!isEmpty(item.header)) header[i] = item.header
        if (!isEmpty(item.prop)) propMap[item.prop] = i
        if (item.merge) needMerge[i] = item.prop
        colWidth[i] = {width: item.width || 20, style: defaultCellStyle}
    })

    sheet.columns = colWidth

    let merge = []

    if (mergeOption && needMerge.length > 0) {
        const {primaryKey, orderKey} = mergeOption
        merge = mergeExcel(needMerge, data, primaryKey, orderKey, headerRows)
    }

    const result = jsonArray2rowArray(data, propMap)

    if (headerRows > 0) {
        sheet.addRow(header)
        header.forEach((_, index) => {
            const cell = sheet.getCell(number2excelColumnHeader(index) + '1')
            Object.keys(defaultHeaderStyle).forEach(key => {
                cell[key] = defaultHeaderStyle[key]
            })
        })
    }

    sheet.addRows(result)

    //exceljs的合并行必须在数据添加后进行
    merge.forEach(i => sheet.mergeCells(i))

    return workbook
}
