import {createMutations} from "@/util"
import {search} from '@/api/message/user'

const state = {
    unreadCount: 0
}

const mutations = createMutations(state)

const actions = {
    refresh({commit}) {
        search({page: 1, pageSize: 1, unread: true})
            .then(({data}) => commit('unreadCount', data))
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
