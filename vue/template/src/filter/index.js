import {isEmpty, timeFormat} from '@/util'

//时间戳格式化
export function timestamp2Date(timestamp) {
    if (isEmpty(timestamp)) return ''
    return timeFormat(null, new Date(timestamp))
}

//秒数格式化，最低时间为分钟
export function secondFormat(time) {
    if (!time || isNaN(time)) return ' NaN'
    if (time < 3600) return ~~(time / 60) + ' 分'
    if (time < 86400) return ~~(time / 3600) + ' 小时'
    return ~~(time / 86400) + ' 天'
}

//格式化当前和给定的时间戳之间的秒数差
export function secondBetweenFormat(time) {
    const between = Date.now() / 1000 - Number(time)
    return secondFormat(between)
}

/**
 * 数字转存储单位，1024 => KB
 *
 * @param num {number}
 * @param digits {number}  保留几位小数
 * @return {string}
 */
export function number2StorageUnit(num, digits = 2) {
    if (isEmpty(num)) return ''
    const si = [
        {value: 1024 * 1024 * 1024 * 1024, symbol: 'TB'},
        {value: 1024 * 1024 * 1024, symbol: 'GB'},
        {value: 1024 * 1024, symbol: 'MB'},
        {value: 1024, symbol: 'KB'}
    ]
    for (let i = 0; i < si.length; i++) {
        if (num >= si[i].value) {
            return (num / si[i].value).toFixed(digits).replace(/\.0+$|(\.[0-9]*[1-9])0+$/, '$1') + si[i].symbol
        }
    }
    return num.toString()
}

/**
 * 10000 => "10,000"
 * @param num {number}
 */
export function toThousandFilter(num) {
    if (isEmpty(num)) return ''
    return (+num || 0).toString().replace(/^-?\d+/g, m => m.replace(/(?=(?!\b)(\d{3})+$)/g, ','))
}

/**
 * Upper case first char
 * @param string {string}
 */
export function uppercaseFirst(string) {
    if (isEmpty(string)) return ''
    return string.charAt(0).toUpperCase() + string.slice(1)
}

export default function (Vue) {
    Object
        .entries({timestamp2Date, secondFormat, secondBetweenFormat, number2StorageUnit, toThousandFilter, uppercaseFirst})
        .forEach(([key, value]) => Vue.filter(key, value))
}
