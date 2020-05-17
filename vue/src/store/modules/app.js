import {getLocalPersonalSettings} from "@/utils/localStorage"

const localSettings = getLocalPersonalSettings()

const state = {
    device: 'pc',
    //登陆页背景动画
    loginPageBackgroundAnimation: 'reflectRain',
    //注册页背景动画
    registerPageBackgroundAnimation: 'firework',
    //路由页面滚动高度
    scrollTop: 0,
    //右侧块是否含有头部
    hasHeader: !!!localSettings.headerAutoHidden,
}

const mutations = {
    setDevice: (state, device) => {
        state.device = device
    },
    setLoginPageBackgroundAnimation: (state, value) => {
        state.loginPageBackgroundAnimation = value
    },
    setRegisterPageBackgroundAnimation: (state, value) => {
        state.registerPageBackgroundAnimation = value
    },
    setScrollTop(state, scrollTop) {
        state.scrollTop = scrollTop
    },
    setHasHeader(state, hasHeader) {
        state.hasHeader = hasHeader
    }
}


export default {
    namespaced: true,
    state,
    mutations
}
