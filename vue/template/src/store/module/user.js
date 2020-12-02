import {createMutations} from "@/store/util"
import {emptyOrDefault} from "@/util"
import {getUser, setUser} from "@/util/storage"
import {mutations as tagsViewMutations} from "@/layout/store/tagsView"
import {elError} from "@/util/message"

//刷新时从本地存储中获取用户信息
const user = getUser()

const state = {
    //是否在退出的过程中，避免重复弹框
    prepareLogout: false,

    /*用户基本信息*/
    id: emptyOrDefault(user.id, null),
    name: emptyOrDefault(user.name),
    avatar: emptyOrDefault(user.avatar),
    admin: emptyOrDefault(user.admin, false),
    token: emptyOrDefault(user.token),
    resources: emptyOrDefault(user.resources, {})
}

const mutations = createMutations(state, true)

const actions = {
    login({commit, dispatch}, userInfo) {
        const {username, password} = userInfo
        if (username !== 'admin') {
            elError('登录时请用admin作为用户名！')
            return Promise.reject()
        }
        const user = {id: 1, admin: true, name: username, avatar: null, token: 'token'}
        commit('$all', user)
        setUser(user)
        return Promise.resolve()
    },

    logout({commit, state, dispatch}) {
        return new Promise((resolve, reject) => {
            if (state.prepareLogout) return Promise.reject()
            commit('prepareLogout', true)
            commit('resource/init', false, {root: true})
            Promise.all([
                dispatch('removeUser'),
                tagsViewMutations.delAllTagAndCache()
            ])
                .then(() => {
                    resolve()
                    window.location.reload()
                })
                .finally(() => commit('prepareLogout', false))
        })
    },

    refresh({state}) {
        setUser(state)
    },

    removeUser({commit}) {
        commit('$all', {resources: {}})
        setUser()
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
