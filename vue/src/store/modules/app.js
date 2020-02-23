import {getLocalPersonalSettings} from "@/utils/localStorage"

const localSettings = getLocalPersonalSettings()

const state = {
    device: 'pc',
    //路由页面滚动高度
    scrollTop: 0,
    //右侧块是否含有头部
    hasHeader: !!!localSettings.headerAutoHidden,
}

const mutations = {
    SET_DEVICE: (state, device) => {
        state.device = device
    },
    SET_SCROLLTOP(state, scrollTop) {
        state.scrollTop = scrollTop
    },
    SET_HASHEADER(state, hasHeader) {
        state.hasHeader = hasHeader
    }
}


export default {
    namespaced: true,
    state,
    mutations
}
