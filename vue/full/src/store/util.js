import {getInitialValue} from "@/util"

//vuex中根据state批量生成对应的mutation
export function createMutations(state, all = false) {
    const keys = Object.keys(state)
    const obj = {}
    keys.forEach(key => {
        obj[key] = (s, v) => s[key] = v
    })
    if (all) {
        obj['$all'] = (s, v) => {
            keys.forEach(key => {
                s[key] = v && v[key] || getInitialValue(s[key])
            })
        }
    }
    return obj
}
