import {isEmpty} from "@/utils"
import {download} from "@/utils/file"
import request from "@/config/request"
import {elError} from "@/utils/message"

/**
 * 搜索表格页导出excel
 * 响应头为json时进行前端导出，否则直接下载文件
 * @param url 搜索的请求地址
 * @param searchForm 搜索参数
 * @param options 合并选项{column, merge}，只有前端导出时才需要
 */
export function exportExcel(url, searchForm, options) {
    request({url, method: 'post', responseType: 'blob', data: searchForm})
        .then(({headers, data}) => {
            let contentType = headers['content-type']
            const contentDisposition = headers['content-disposition']
            const filename = window.decodeURI(contentDisposition.split('=')[1])

            if (contentType.includes('json')) {
                const reader = new FileReader()
                reader.onload = () => {
                    const response = JSON.parse(reader.result)
                    if (response.status !== 200) return elError(response.msg)
                    let {column, merge} = options
                    const sheet = json2sheet(column, response.data, merge)
                    exportExcelByJs(sheet, filename)
                }
                reader.readAsText(data)
            }
            else download(data, filename)
        })
        .catch(e => elError(e))
}

//纯前端导出excel
export function exportExcelByJs(sheet, filename) {
    const wb = {
        SheetNames: ['Sheet1'],
        Sheets: {Sheet1: sheet}
    }
    const out = window.XLSX.write(wb, {
        bookType: 'xlsx',
        bookSST: false,
        type: 'binary'
    })
    download(new Blob([s2ab(out)], {type: "application/octet-stream"}), filename)
}

/**
 * json数组转sheet
 * @param column      列配置
 * @param data        json数组
 * @param mergeOption 合并配置项
 * @param headerRows  表头占的行数
 * @returns
 */
export function json2sheet(column, data, mergeOption, headerRows = 1) {
    const header = []
    const propMap = {}
    const colWidth = []
    const needMerge = []
    column.forEach((item, i) => {
        if (!isEmpty(item.header)) header[i] = item.header
        if (!isEmpty(item.prop)) propMap[item.prop] = i
        if (item.merge) needMerge[i] = item.prop
        colWidth[i] = {wch: item.width || 20}
    })

    let mergeResult = []

    if (mergeOption && needMerge.length > 0) {
        mergeResult = mergeExcel(needMerge, data, mergeOption.primaryKey, mergeOption.orderKey, headerRows)
    }

    let result = data.map(i => Object.keys(i).reduce((arr, key) => {
        arr[propMap[key]] = i[key]
        return arr
    }, []))

    headerRows && result.unshift(header)

    const sheet = aoa_to_sheet(result, headerRows)
    sheet['!cols'] = colWidth
    sheet['!merges'] = mergeResult
    return sheet
}

/**
 * 合并excel
 * @param primaryKey   该行json的唯一标识字段名
 * @param indexKey     该行json的序号字段名
 * @param prop         表头的属性名称，用于映射json
 * @param data         json数组
 * @param ignoreRows   忽略的行数，默认为1（忽略表头）
 */
function mergeExcel(prop, data, primaryKey, indexKey, ignoreRows = 1) {
    const result = []
    const startRows = ignoreRows + 1

    const merge = prop.map(_ => isEmpty(_) ? undefined : [1])
    const temp = prop.map(_ => isEmpty(_) ? undefined : 1)

    //序号从1开始
    let indexAfterMerge = 1
    data[0][indexKey] = indexAfterMerge

    function compare(currentRow, lastRow, nextRow, colIndex) {
        const attr = prop[colIndex]

        //若与上一行的primaryKey相同且属性相同，则temp对应项+1
        if (currentRow[primaryKey] === lastRow[primaryKey]
            && currentRow[attr] === lastRow[attr]) {
            temp[colIndex]++
            //最后一行特殊处理
            if (!nextRow) {
                merge[colIndex].push(temp[colIndex])
            }
            else {
                //若与下一行相同
                if (currentRow[primaryKey] === nextRow[primaryKey]
                    && currentRow[attr] === nextRow[attr]) {
                    merge[colIndex].push(1)
                }
                //否则存入
                else merge[colIndex].push(temp[colIndex])
            }
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
            currentRow[indexKey] = lastRow[indexKey]
        }
        else currentRow[indexKey] = ++indexAfterMerge
        if (nextRow && nextRow[primaryKey] === currentRow[primaryKey]) {
            nextRow[indexKey] = currentRow[indexKey]
        }

        prop.forEach((_, colIndex) => !isEmpty(_) && compare(currentRow, lastRow, nextRow, colIndex))
    }

    function number2excelColumnHeader(n) {
        let s = ""
        while (n >= 0) {
            s = String.fromCharCode(n % 26 + 65) + s
            n = Math.floor(n / 26) - 1
        }
        return s
    }

    function mergeResultConstructor(arr, colIndex) {
        const colHeader = number2excelColumnHeader(colIndex)
        arr.forEach((colspan, rowIndex) => {
            if (colspan > 1) {
                const start = colHeader + (startRows + rowIndex - (colspan - 1))
                const end = colHeader + (startRows + rowIndex)
                const str = `${start}:${end}`
                result.push(XLSX.utils.decode_range(str))
            }
        })
    }

    merge.forEach((arr, index) => arr && mergeResultConstructor(arr, index))

    return result
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
            const cell_ref = XLSX.utils.encode_cell({c: C, r: R})

            /* TEST: proper cell types and value handling */
            if (typeof cell.v === 'number') cell.t = 'n'
            else if (typeof cell.v === 'boolean') cell.t = 'b'
            else if (cell.v instanceof Date) {
                cell.t = 'n'
                cell.z = XLSX.SSF._table[14]
                cell.v = datenum(cell.v)
            }
            else cell.t = 's'
            ws[cell_ref] = cell
        }
    }

    /* TEST: proper range */
    if (range.s.c < 10000000) ws['!ref'] = XLSX.utils.encode_range(range)
    return ws
}

function s2ab(s) {
    const buf = new ArrayBuffer(s.length)
    const view = new Uint8Array(buf)
    for (let i = 0; i !== s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF
    return buf
}
