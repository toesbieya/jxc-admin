import {createMutations, emptyOrDefault} from "@/utils"
import {autoCompleteUrl} from "@/utils/file"
import {getUser, setUser} from "@/utils/storage"
import {login, logout} from '@/api/account'

//刷新时从本地存储中获取用户信息
const user = getUser()

const state = {
    //是否在退出的过程中，避免重复弹框
    prepare_logout: '',

    /*用户基本信息*/
    id: emptyOrDefault(user.id, null),
    name: emptyOrDefault(user.name),
    role_name: emptyOrDefault(user.role_name),
    avatar: emptyOrDefault(user.avatar),
    admin: emptyOrDefault(user.admin),
    token: emptyOrDefault(user.token),
    resources: emptyOrDefault(user.resources, {})
}

const mutations = createMutations(state, true)

const actions = {
    login({commit, dispatch}, userInfo) {
        const {username, password} = userInfo
        return new Promise((resolve, reject) => {
            login({username: username.trim(), password})
                .then(user => {
                    user.admin === 1 && (user.role_name = '超级管理员')
                    user.avatar = autoCompleteUrl(user.avatar)
                    commit('$all', user)
                    setUser(user)
                    return dispatch('socket/init', user, {root: true})
                })
                .then(() => resolve())
                .catch(error => reject(error))
        })
    },

    logout({commit, state, dispatch}) {
        return new Promise((resolve, reject) => {
            if (state.prepare_logout) return Promise.reject()
            commit('prepare_logout', 'yes')
            logout(state.token)
                .then(() => {
                    commit('resource/hasInitRoutes', false, {root: true})
                    return Promise.all([
                        dispatch('socket/close', null, {root: true}),
                        dispatch('removeUser'),
                        dispatch('tagsView/delAllViews', null, {root: true})
                    ])
                })
                .then(() => {
                    resolve()
                    window.location.reload()
                })
                .catch(error => reject(error))
                .finally(() => commit('prepare_logout', ''))
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
