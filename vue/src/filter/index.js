import {isEmpty, timeFormat} from '@/utils'

export function timestamp2Date(timestamp) {
    if (isEmpty(timestamp)) return ''
    return timeFormat(null, new Date(timestamp))
}

/*经过多长时间，单位秒，最低时间为分钟*/
export function timePass(time) {
    if (!time || isNaN(time)) return 'NaN'
    if (time < 3600) return ~~(time / 60) + ' 分'
    else if (time < 86400) return ~~(time / 3600) + ' 小时'
    else return ~~(time / 86400) + ' 天'
}

export function timeAgo(time) {
    const between = Date.now() / 1000 - Number(time)
    if (between < 3600) {
        return ~~(between / 60) + ' 分钟'
    }
    else if (between < 86400) {
        return ~~(between / 3600) + ' 小时'
    }
    else {
        return ~~(between / 86400) + ' 天'
    }
}

export function numberFormatter(num, digits = 2) {
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
 * @param {number} num
 */
export function toThousandFilter(num) {
    if (isEmpty(num)) return ''
    return (+num || 0).toString().replace(/^-?\d+/g, m => m.replace(/(?=(?!\b)(\d{3})+$)/g, ','))
}

/**
 * Upper case first char
 * @param {String} string
 */
export function uppercaseFirst(string) {
    if (isEmpty(string)) return ''
    return string.charAt(0).toUpperCase() + string.slice(1)
}
