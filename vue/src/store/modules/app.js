import {getLocalPersonalSettings} from "@/utils/localStorage"
import {createMutations} from "@/utils"

const localSettings = getLocalPersonalSettings()

const state = {
    //区分pc和移动端（mobile）
    device: 'pc',
    //登陆页背景动画
    loginPageBackgroundAnimation: 'firework',
    //注册页背景动画
    registerPageBackgroundAnimation: 'firework',
    //路由页面滚动高度
    scrollTop: 0,
    //右侧块是否含有头部
    hasHeader: !!!localSettings.headerAutoHidden,
}

const mutations = createMutations(state)

export default {
    namespaced: true,
    state,
    mutations
}
