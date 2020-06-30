/**
 * @deprecated
 * xlsx问题多多，转向excelJS
 */

import {isEmpty} from "@/utils"
import {download} from "@/utils/file"
import {abstractExportExcel, jsonArray2rowArray, mergeExcel} from "./common"

/**
 * @param url
 * @param searchForm
 * @param options
 */
export function exportExcel(url, searchForm, options) {
    abstractExportExcel(url, searchForm, options, json2workbook, exportExcelByJs)
}

//纯前端导出excel
export function exportExcelByJs(workbook, filename) {
    const out = window.XLSX.write(workbook, {
        bookType: 'xlsx',
        bookSST: false,
        type: 'binary'
    })
    download(new Blob([s2ab(out)], {type: "application/octet-stream"}), filename)
}

/**
 * @param data        json数组
 * @param columns     列配置
 * @param merge       合并配置项
 * @returns
 */
export function json2workbook(data, columns, merge) {
    const header = []    //表格头部
    const propMap = {}   //表头和字段名的映射表，propMap[字段名]=对应的表头的数组下标
    const columnSettings = []  //列宽配置，默认20
    const needMerge = [] //合并行配置，[i]=字段名

    columns.forEach((col, i) => {
        if (!isEmpty(col.header)) header[i] = col.header
        if (!isEmpty(col.prop)) propMap[col.prop] = i
        if (col.merge) needMerge[i] = col.prop
        columnSettings[i] = {wch: col.width || 20}
    })

    const headerRows = header.length > 0 ? 1 : 0

    let bodyMerge = []

    if (merge && needMerge.length > 0) {
        const {primaryKey, orderKey} = merge
        bodyMerge = mergeExcel(needMerge, data, primaryKey, orderKey, headerRows)
    }

    const result = jsonArray2rowArray(data, propMap)

    headerRows && result.unshift(header)

    const sheet = aoa_to_sheet(result, headerRows)
    sheet['!cols'] = columnSettings
    sheet['!merges'] = bodyMerge.map(i => window.XLSX.utils.decode_range(i))
    return {SheetNames: ['Sheet1'], Sheets: {Sheet1: sheet}}
}

//默认的excel单元格样式
const defaultCellStyle = {
    font: {name: '宋体'},
    alignment: {
        wrapText: 1,// 自动换行
        horizontal: "center",
        vertical: "center",
        indent: 0
    }
}

//默认的头部样式
const defaultHeaderStyle = {
    ...defaultCellStyle,
    fill: {fgColor: {rgb: "C0C0C0"}},
    font: {name: '宋体', sz: 14, bold: true},
    border: {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}}
}

function datenum(v, date1904) {
    if (date1904) v += 1462
    const epoch = Date.parse(v)
    return (epoch - new Date(Date.UTC(1899, 11, 30))) / (24 * 60 * 60 * 1000)
}

function aoa_to_sheet(data, headerRows) {
    const ws = {}
    const range = {s: {c: 10000000, r: 10000000}, e: {c: 0, r: 0}}
    for (let R = 0; R !== data.length; ++R) {
        for (let C = 0; C !== data[R].length; ++C) {
            if (range.s.r > R) range.s.r = R
            if (range.s.c > C) range.s.c = C
            if (range.e.r < R) range.e.r = R
            if (range.e.c < C) range.e.c = C
            //生成cell的时候，使用默认样式
            const cell = {
                v: data[R][C],
                s: R === 0 && headerRows > R ? defaultHeaderStyle : defaultCellStyle
            }
            if (cell.v == null) continue
            const cell_ref = window.XLSX.utils.encode_cell({c: C, r: R})

            /* TEST: proper cell types and value handling */
            if (typeof cell.v === 'number') cell.t = 'n'
            else if (typeof cell.v === 'boolean') cell.t = 'b'
            else if (cell.v instanceof Date) {
                cell.t = 'n'
                cell.z = window.XLSX.SSF._table[14]
                cell.v = datenum(cell.v)
            }
            else cell.t = 's'
            ws[cell_ref] = cell
        }
    }

    /* TEST: proper range */
    if (range.s.c < 10000000) ws['!ref'] = window.XLSX.utils.encode_range(range)
    return ws
}

function s2ab(s) {
    const buf = new ArrayBuffer(s.length)
    const view = new Uint8Array(buf)
    for (let i = 0; i !== s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF
    return buf
}
