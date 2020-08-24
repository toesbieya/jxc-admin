import {getLocalPersonalSettings} from "@/util/storage"
import {createMutations} from "@/util"
import {isMobile} from "@/util/browser"

const localSettings = getLocalPersonalSettings()

const state = {
    //区分pc和移动端（mobile）
    device: isMobile() ? 'mobile' : 'pc',

    //登陆页背景动画
    loginBackgroundAnimation: 'sparkRain',

    //右侧块是否含有头部
    hasHeader: !!!localSettings.headerAutoHidden
}

const mutations = createMutations(state)

export default {
    namespaced: true,
    state,
    mutations
}
