/*基于decimal.js的运算封装*/
import Decimal from 'decimal.js'

/**
 * 依次相加
 *
 * @param val {number}
 * @return {number}
 */
export function plus(...val) {
    const result = val.reduce((total, cur) => total.add(new Decimal(cur)), new Decimal(0))
    return result.toNumber()
}

/**
 * 依次相减
 *
 * @param val {number}
 * @return {number}
 */
export function sub(...val) {
    if (val.length === 0) return 0

    let result = new Decimal(val[0])
    for (let i = 1; i < val.length; i++) {
        result = result.sub(new Decimal(val[i]))
    }
    return result.toNumber()
}

/**
 * 依次相乘
 *
 * @param val {number}
 * @return {number}
 */
export function mul(...val) {
    const result = val.reduce((total, cur) => total.mul(new Decimal(cur)), new Decimal(1))
    return result.toNumber()
}

/**
 * 依次相除
 *
 * @param val {number}
 * @return {number}
 */
export function div(...val) {
    if (val.length === 0) return 0

    let f = new Decimal(val[0])
    for (let i = 1; i < val.length; i++) {
        f = f.div(new Decimal(val[i]))
    }
    return f.toNumber()
}
