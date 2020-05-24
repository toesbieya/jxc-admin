/*
* 个性化设置
* */

import {isEmpty} from "@/utils"
import {getLocalPersonalSettings, setLocalPersonalSettings} from "@/utils/localStorage"

const localSettings = getLocalPersonalSettings()

function createMutations(state) {
    return Object.keys(state).reduce((mutations, key) => {
        mutations[key] = (ref, data) => {
            ref[key] = data
            setLocalPersonalSettings(ref)
        }
        return mutations
    }, {})
}

const state = {
    //是否显示logo
    showLogo: true,
    //是否显示面包屑
    showBreadcrumb: true,
    //左侧菜单手风琴效果
    sidebarUniqueOpen: true,
    //左侧菜单是否折叠
    sidebarCollapse: false,
    //左侧菜单折叠时，弹出菜单是否显示上级
    sidebarShowParent: false,
    //是否自动隐藏左侧菜单
    sidebarAutoHidden: false,
    //是否自动隐藏头部
    headerAutoHidden: false,
    //是否显示返回顶部按钮
    showBackToTop: true,
}

//state填入初始值
Object.keys(state).forEach(key => {
    if (!isEmpty(localSettings[key])) state[key] = localSettings[key]
})

const mutations = {
    ...createMutations(state),
    sidebarCollapse(state, v) {
        if (v && state.sidebarAutoHidden) {
            state.sidebarAutoHidden = false
        }
        state.sidebarCollapse = v
        setLocalPersonalSettings(state)
    },
    sidebarAutoHidden(state, v) {
        if (v && state.sidebarCollapse) {
            state.sidebarCollapse = false
        }
        state.sidebarAutoHidden = v
        setLocalPersonalSettings(state)
    }
}

export default {
    namespaced: true,
    state,
    mutations
}
