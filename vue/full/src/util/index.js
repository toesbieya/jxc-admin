/**
 * 判断是否为空值，undefined、null、'' 都视为空值
 *
 * @param str         不定参数
 * @return {boolean}  若为空值，返回true，否则返回false
 */
export function isEmpty(...str) {
    return str.some(i => i === undefined || i === null || i === '')
}

/**
 * 根据传入值的类型，返回基础起始值
 *
 * @param v
 * @return {boolean|{}|string|*[]|number|null}
 */
export function getInitialValue(v) {
    if (v === undefined || v === null) return null
    if (typeof v === 'string') return ''
    if (typeof v === 'boolean') return false
    if (typeof v === 'number') return 0
    if (typeof v === 'object') return {}
    if (Array.isArray(v)) return []
}

/**
 * 防抖
 *
 * @param func {function}      原函数
 * @param wait {number}        防抖间隔，单位毫秒
 * @param immediate {boolean}  是否立即执行一次
 * @return {function}          经过防抖包装后的函数
 */
export function debounce(func, wait = 100, immediate = false) {
    let timeout, args, context, timestamp, result

    const later = function () {
        // 据上一次触发时间间隔
        const last = new Date().getTime() - timestamp

        // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
        if (last < wait && last > 0) {
            timeout = window.setTimeout(later, wait - last)
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
        if (!timeout) timeout = window.setTimeout(later, wait)
        if (callNow) {
            result = func.apply(context, args)
            context = args = null
        }

        return result
    }
}

//将传入对象的所有函数的this绑定为其自身
export function bindThis(obj, root = obj) {
    if (!obj || typeof obj !== 'object') return

    Object.entries(obj).forEach(([k, v]) => {
        if (typeof v === 'function') {
            obj[k] = v.bind(root)
        }
        bindThis(v, root)
    })

    return obj
}

export function deepClone(source) {
    if (source === null || typeof source !== 'object' || source instanceof Promise) {
        return source
    }

    if (Array.isArray(source)) {
        return source.map(i => deepClone(i))
    }

    return Object.keys(source).reduce((obj, key) => {
        obj[key] = deepClone(source[key])
        return obj
    }, {})
}

/**
 * 当传入空值时，返回默认值
 *
 * @param v             传入值
 * @param defaultValue  当传入值为空值(使用{@link #isEmpty}判断)时，返回的值
 * @return {string|*}
 */
export function emptyOrDefault(v, defaultValue = '') {
    return isEmpty(v) ? defaultValue : v
}

/**
 * 字符串替换全部
 *
 * @param str              原字符串
 * @param substr           被替换的字符串
 * @param replacement      替换的字符串
 * @return {string}
 */
export function replaceAll(str, substr, replacement) {
    if (str == null) return str

    return str.replace(new RegExp(substr, 'gm'), replacement)
}

/**
 * 简单重置对象属性，遇到对象时会递归重置
 * 重置方法使用{@link #getInitialValue}
 *
 * @param obj  需要重置的对象
 */
export function resetObj(obj) {
    if (isEmpty(obj)) return
    Object.keys(obj).forEach(key => {
        if (obj[key] !== null && typeof obj[key] === 'object') {
            resetObj(obj[key])
        }
        else obj[key] = getInitialValue(obj[key])
    })
}

/**
 * 将source合并到target中
 * 仅对target中存在的键进行合并
 * 仅当遇到对象值时进行递归
 *
 * @param target
 * @param source
 */
export function mergeObj(target, source) {
    if (isEmpty(target, source)) return

    for (const key of Object.keys(target)) {
        if (!(key in source)) continue

        //数组类型直接赋值，不做深拷贝
        if (Array.isArray(target[key])) {
            target[key] = source[key] || []
            continue
        }

        //number类型不考虑NaN
        if (typeof target[key] === 'number') {
            target[key] = source[key] || 0
            continue
        }

        //非空对象递归处理
        if (target[key] !== null && typeof target[key] === 'object') {
            mergeObj(target[key], source[key])
            continue
        }

        target[key] = source[key]
    }
}

/**
 * 日期格式化
 *
 * @param fmt {*|string}  格式，y(+..y)-年、M(+M)-月、d(+d)-天、H(+H)-时、m(+m)-分、s(+s)-秒、S-毫秒
 * @param date {Date}     可选，被格式化的日期
 * @return {string}       格式化后的日期字符串
 */
export function timeFormat(fmt, date = new Date()) {
    if (isEmpty(fmt)) fmt = 'yyyy-MM-dd HH:mm:ss'

    const o = {
        "M+": date.getMonth() + 1, //月份
        "d+": date.getDate(), //日
        "H+": date.getHours(), //小时
        "m+": date.getMinutes(), //分
        "s+": date.getSeconds(), //秒
        "S": date.getMilliseconds() //毫秒
    }

    if (/(y+)/.test(fmt)) {
        const replace = (date.getFullYear() + "").substring(4 - RegExp.$1.length)
        fmt = fmt.replace(RegExp.$1, [...replace].join(''))
    }

    for (const k in o) {
        if (new RegExp(`(${k})`).test(fmt)) {
            const firstMatch = RegExp.$1
            const replace = firstMatch.length === 1 ? o[k] + "" : ("00" + o[k]).substring(("" + o[k]).length)
            fmt = fmt.replace(firstMatch, [...replace].join(''))
        }
    }

    return fmt
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

        window.clearTimeout(timeoutID)

        if (elapsed > delay) exec()
        else timeoutID = window.setTimeout(exec, delay - elapsed)
    }

    return wrapper
}

/**
 * 循环等待成功事件
 *
 * @param success {function}   返回true时说明成功
 * @param callback {function}  成功后的回调
 * @param interval {number}    循环间隔，毫秒
 * @param maxTryTime {number}  最大循环次数，超出reject，小于1视为Infinity
 * @return {Promise}
 */
export function waitUntilSuccess(success, callback, interval = 1000, maxTryTime = 0) {
    return new Promise((resolve, reject) => {
        let fun, count = 0

        const check = () => {
            if (success()) {
                window.clearInterval(fun)
                typeof callback === 'function' && callback()
                return resolve()
            }
            if (maxTryTime >= 1 && ++count >= maxTryTime) {
                return reject()
            }
        }

        if (success()) return check()

        fun = window.setInterval(check, interval)
    })
}
