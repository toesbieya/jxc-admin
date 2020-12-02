import {createMutations} from "@/store/util"

const state = {
    //登录页背景动画
    loginBackgroundAnimation: 'sparkRain',
}

const mutations = createMutations(state)

export default {
    namespaced: true,
    state,
    mutations
}
