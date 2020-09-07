import {createMutations} from "@/store/util"
import {search} from '@/api/message/user'

const state = {
    unreadCount: 0
}

const mutations = createMutations(state)

const actions = {
    refresh({commit}) {
        search
            .request({page: 1, pageSize: 1, unread: true})
            .then(({data}) => commit('unreadCount', data.total))
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
