import Vue from 'vue'
import {isEmpty} from "@/util"
import {createGetters} from "@/util/observable"
import {getLocalPersonalSettings, setLocalPersonalSettings} from "@/util/storage"

const state = {
    //是否显示logo
    showLogo: true,

    //是否显示面包屑
    showBreadcrumb: true,

    //是否使用多页签
    useTagsView: true,

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
const localSettings = getLocalPersonalSettings()
Object.keys(state).forEach(key => {
    if (!isEmpty(localSettings[key])) state[key] = localSettings[key]
})

const store = Vue.observable(state)

//每次修改个人设置时，同步到localStorage
function createMutations(store) {
    return Object.keys(store).reduce((mutations, key) => {
        mutations[key] = data => {
            store[key] = data
            setLocalPersonalSettings(store)
        }
        return mutations
    }, {})
}

export const getters = createGetters(store)

export const mutations = {
    ...createMutations(store),

    sidebarCollapse(v) {
        //当启用侧边栏折叠时，停用自动隐藏侧边栏
        if (v) store.sidebarAutoHidden = false
        store.sidebarCollapse = v
        setLocalPersonalSettings(store)
    },

    sidebarAutoHidden(v) {
        //当启用自动隐藏侧边栏时，停用侧边栏折叠
        if (v) store.sidebarCollapse = false
        store.sidebarAutoHidden = v
        setLocalPersonalSettings(store)
    }
}
