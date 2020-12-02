import {getInitialValue} from "@/util"

//为Vue.observer返回的对象设置getter
export function createGetters(store) {
    const getters = Object.create({})
    Object.defineProperties(
        getters,
        Object.keys(store).reduce((obj, key) => {
            obj[key] = {
                enumerable: true,
                get() {
                    return store[key]
                }
            }
            return obj
        }, {})
    )
    return getters
}

//设置mutation
export function createMutations(store, all = false) {
    const keys = Object.keys(store)
    const obj = {}
    keys.forEach(key => {
        obj[key] = v => store[key] = v
    })
    if (all) {
        obj['$all'] = v => {
            keys.forEach(key => {
                store[key] = v && v[key] || getInitialValue(store[key])
            })
        }
    }
    return obj
}
