import {createMutations} from "@/util"

const state = {
    //登陆页背景动画
    loginBackgroundAnimation: 'sparkRain',
}

const mutations = createMutations(state)

export default {
    namespaced: true,
    state,
    mutations
}
