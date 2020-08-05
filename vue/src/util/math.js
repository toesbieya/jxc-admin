/*基于decimal.js的运算封装*/
import Decimal from 'decimal.js'

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
