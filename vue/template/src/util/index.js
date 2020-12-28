import {isEmpty, getInitialValue, debounce, deepClone} from "el-admin-layout/src/util"

export {isEmpty, getInitialValue, debounce, deepClone}

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
