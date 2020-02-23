import pako from 'pako'
import Decimal from 'decimal.js'

export function isEmpty(...str) {
    return str.some(i => i === undefined || i === null || i === '')
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
        else obj[key] = null
    })
}

//简单合并对象
export function mergeObj(target, source) {
    Object.keys(target).forEach(key => {
        if (key in source) {
            if (Array.isArray(target[key])) {
                target[key] = source[key] || []
            }
            else if (typeof target[key] === 'number') {
                target[key] = source[key] || 0
            }
            else target[key] = source[key]
        }
    })
}

export function timeFormat(fmt = 'yyyy-MM-dd HH:mm:ss', date = new Date()) {
    if (isEmpty(fmt)) fmt = 'yyyy-MM-dd HH:mm:ss'
    let o = {
        "M+": date.getMonth() + 1, //月份
        "d+": date.getDate(), //日
        "H+": date.getHours(), //小时
        "m+": date.getMinutes(), //分
        "s+": date.getSeconds(), //秒
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds() //毫秒
    }
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length))
    for (let k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)))
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

export function unzip(b64Data) {
    let strData = atob(b64Data)
    // Convert binary string to character-number array
    let charData = strData.split('').map(x => x.charCodeAt(0))
    // Turn number array into byte-array
    let binData = new Uint8Array(charData)
    // unzip
    let data = pako.inflate(binData)
    // Convert gunzipped byteArray back to ascii string:
    strData = String.fromCharCode.apply(null, new Uint16Array(data))
    return decodeURIComponent(strData)
}

export function zip(str) {
    let binaryString = pako.gzip(encodeURIComponent(str), {to: 'string'})
    return btoa(binaryString)
}

/**
 * 循环等待成功事件
 * @param success 判断是否成功，true or false
 * @param callback 成功后的回调
 * @param interval 循环间隔，毫秒
 */
export function waitUntilSuccess(success, callback, interval = 1000) {
    return new Promise(resolve => {
        let fun = null
        let check = () => {
            if (success()) {
                clearInterval(fun)
                typeof callback === 'function' && callback()
                return resolve()
            }
        }
        if (success()) return check()
        fun = setInterval(check, interval)
    })
}

//store中根据state批量生成对应的mutation
export function createMutations(state, all = true, keyFunction = key => 'SET_' + key.toUpperCase()) {
    const arr = Object.keys(state)
    const obj = {}
    arr.forEach(key => {
        obj[keyFunction(key)] = (s, v) => s[key] = v
    })
    if (all) obj['SET_$ALL'] = (s, v) => arr.forEach(key => s[key] = v ? v[key] || '' : '')
    return obj
}

//将引导步骤中函数的this绑定为组件实例
export function transformGuideSteps(instance, steps) {
    steps.forEach(step => {
        Object.keys(step).forEach(key => {
            if (typeof step[key] === 'function') {
                step[key] = step[key].bind(instance)
            }
        })
    })
}

//删除所有url参数
export function delAllUrlParam() {
    let paramStartIndex = location.href.indexOf('?')
    if (paramStartIndex > -1) {
        history.replaceState(null, null, location.href.substring(0, paramStartIndex))
    }
}


/*基于decimal.js的运算封装*/
export function plus(...val) {
    if (val.length === 0) return 0
    else if (val.length === 1) return new Decimal(val[0]).toNumber()
    let f = new Decimal(val[0])
    for (let i = 1; i < val.length; i++) {
        f = f.add(new Decimal(val[i]))
    }
    return f.toNumber()
}

export function sub(...val) {
    if (val.length === 0) return 0
    else if (val.length === 1) return new Decimal(val[0]).toNumber()
    let f = new Decimal(val[0])
    for (let i = 1; i < val.length; i++) {
        f = f.sub(new Decimal(val[i]))
    }
    return f.toNumber()
}

export function mul(...val) {
    if (val.length === 0) return 0
    else if (val.length === 1) return new Decimal(val[0]).toNumber()
    let f = new Decimal(val[0])
    for (let i = 1; i < val.length; i++) {
        f = f.mul(new Decimal(val[i]))
    }
    return f.toNumber()
}

export function div(...val) {
    if (val.length === 0) return 0
    else if (val.length === 1) return new Decimal(val[0]).toNumber()
    let f = new Decimal(val[0])
    for (let i = 1; i < val.length; i++) {
        f = f.div(new Decimal(val[i]))
    }
    return f.toNumber()
}


export function deepClone(data) {
    if (data === null || data === undefined) {
        return undefined
    }

    const dataType = getDataType(data)

    if (dataType === "Date") {
        let clonedDate = new Date()
        clonedDate.setTime(data.getTime())
        return clonedDate
    }

    if (dataType === "Object") {
        if (isCyclic(data) === true) {
            return data
        }

        let copiedObject = {}

        Object.keys(data).forEach(key => copiedObject[key] = deepClone(data[key]))

        return copiedObject
    }

    if (dataType === "Array") {
        let copiedArray = []

        for (let i = 0; i < data.length; i++) {
            copiedArray.push(deepClone(data[i]))
        }

        return copiedArray
    }

    else return data
}

function getDataType(data) {
    return Object.prototype.toString.call(data).slice(8, -1)
}

function isCyclic(data) {
    let seenObjects = []

    function detect(data) {
        if (data && getDataType(data) === "Object") {

            if (seenObjects.indexOf(data) !== -1) {
                return true
            }

            seenObjects.push(data)

            for (let key in data) {
                if (data.hasOwnProperty(key) && detect(data[key])) {
                    return true
                }
            }
        }
        return false
    }

    return detect(data)
}



