export function isEmpty(...str) {
    return str.some(i => i === undefined || i === null || i === '')
}

export function emptyOrDefault(v, defaultValue = '') {
    return isEmpty(v) ? defaultValue : v
}

//简单重置对象属性
export function resetObj(obj) {
    if (isEmpty(obj)) return
    Object.keys(obj).forEach(key => {
        if (Array.isArray(obj[key])) {
            obj[key] = []
        }
        else if (typeof obj[key] === 'number') {
            obj[key] = 0
        }
        else if (typeof obj[key] === 'boolean') {
            obj[key] = false
        }
        else if (obj[key] !== null && typeof obj[key] === 'object') {
            resetObj(obj[key])
        }
        else obj[key] = null
    })
}

//简单合并对象
export function mergeObj(target, source) {
    Object.keys(target).forEach(key => {
        if (!isEmpty(source) && key in source) {
            if (Array.isArray(target[key])) {
                target[key] = source[key] || []
            }
            else if (typeof target[key] === 'number') {
                target[key] = source[key] || 0
            }
            else if (target[key] !== null && typeof target[key] === 'object') {
                mergeObj(target[key], source[key])
            }
            else {
                if (typeof source[key] === 'function') {
                    target[key] = source[key].toString()
                }
                else target[key] = source[key]
            }
        }
    })
}

export function timeFormat(fmt, date = new Date()) {
    if (isEmpty(fmt)) fmt = 'yyyy-MM-dd HH:mm:ss'

    const o = {
        "M+": date.getMonth() + 1, //月份
        "d+": date.getDate(), //日
        "H+": date.getHours(), //小时
        "m+": date.getMinutes(), //分
        "s+": date.getSeconds(), //秒
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds() //毫秒
    }

    if (/(y+)/.test(fmt)) {
        const replace = (date.getFullYear() + "").substring(4 - RegExp.$1.length)
        fmt = fmt.replace(RegExp.$1, [...replace].join(''))
    }

    for (const k in o) {
        if (new RegExp(`(${k})`).test(fmt)) {
            const firstMatch = RegExp.$1
            const replace = firstMatch.length === 1 ? o[k] : ("00" + o[k]).substring(("" + o[k]).length)
            fmt = fmt.replace(firstMatch, [...replace].join(''))
        }
    }

    return fmt
}

export function debounce(func, wait = 100, immediate = false) {
    let timeout, args, context, timestamp, result

    const later = function () {
        // 据上一次触发时间间隔
        const last = new Date().getTime() - timestamp

        // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
        if (last < wait && last > 0) {
            timeout = setTimeout(later, wait - last)
        }
        else {
            timeout = null
            // 如果设定为immediate===true，因为开始边界已经调用过了此处无需调用
            if (!immediate) {
                result = func.apply(context, args)
                if (!timeout) context = args = null
            }
        }
    }

    return function () {
        context = this
        args = arguments
        timestamp = new Date().getTime()
        const callNow = immediate && !timeout
        // 如果延时不存在，重新设定延时
        if (!timeout) timeout = setTimeout(later, wait)
        if (callNow) {
            result = func.apply(context, args)
            context = args = null
        }

        return result
    }
}

export function throttle(func, delay = 100) {
    let timeoutID
    let lastExec = 0

    function wrapper() {
        const self = this
        const elapsed = Date.now() - lastExec
        const args = arguments

        function exec() {
            lastExec = Date.now()
            func.apply(self, args)
        }

        clearTimeout(timeoutID)

        if (elapsed > delay) exec()
        else timeoutID = setTimeout(exec, delay - elapsed)
    }

    return wrapper
}

/**
 * 循环等待成功事件
 * @param success 判断是否成功，true or false
 * @param callback 成功后的回调
 * @param interval 循环间隔，毫秒
 * @param maxTryTime 最大循环次数，超出reject，小于1视为Infinity
 */
export function waitUntilSuccess(success, callback, interval = 1000, maxTryTime = 0) {
    return new Promise((resolve, reject) => {
        let fun,
            count = 0

        const check = () => {
            if (success()) {
                window.clearInterval(fun)
                typeof callback === 'function' && callback()
                return resolve()
            }
            if (maxTryTime >= 1) {
                count++
                if (count >= maxTryTime) return reject()
            }
        }

        if (success()) return check()

        fun = setInterval(check, interval)
    })
}

//store中根据state批量生成对应的mutation
export function createMutations(state, all = false) {
    const arr = Object.keys(state)
    const obj = {}
    arr.forEach(key => {
        obj[key] = (s, v) => s[key] = v
    })
    if (all) obj['$all'] = (s, v) => arr.forEach(key => s[key] = v ? v[key] || '' : '')
    return obj
}

//删除所有url参数
export function delAllUrlParam() {
    let paramStartIndex = location.href.indexOf('?')
    if (paramStartIndex > -1) {
        const href = location.href.substring(0, paramStartIndex)
        history.replaceState(null, null, [...href].join(''))
    }
}
