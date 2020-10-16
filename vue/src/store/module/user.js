import {createMutations} from "@/store/util"
import {emptyOrDefault} from "@/util"
import {autoCompleteUrl} from "@/util/file"
import {getUser, setUser} from "@/util/storage"
import {mutations as tagsViewMutations} from "@/layout/store/tagsView"
import {login, logout} from '@/api/account'

//刷新时从本地存储中获取用户信息
const user = getUser()

const state = {
    //是否在退出的过程中，避免重复弹框
    prepareLogout: false,

    /*用户基本信息*/
    id: emptyOrDefault(user.id, null),
    name: emptyOrDefault(user.name),
    roleName: emptyOrDefault(user.roleName),
    deptName: emptyOrDefault(user.deptName),
    avatar: emptyOrDefault(user.avatar),
    admin: emptyOrDefault(user.admin, false),
    token: emptyOrDefault(user.token),
    resources: emptyOrDefault(user.resources, {})
}

const mutations = createMutations(state, true)

const actions = {
    login({commit, dispatch}, userInfo) {
        const {username, password} = userInfo
        return login
            .request({username: username.trim(), password})
            .then(({data: user}) => {
                if (user.admin === true) user.roleName = '超级管理员'
                user.avatar = autoCompleteUrl(user.avatar)
                commit('$all', user)
                setUser(user)
                return dispatch('socket/init', null, {root: true})
            })
    },

    logout({commit, state, dispatch}) {
        return new Promise((resolve, reject) => {
            if (state.prepareLogout) return Promise.reject()
            commit('prepareLogout', true)
            logout
                .request(state.token)
                .then(() => {
                    commit('resource/init', false, {root: true})
                    return Promise.all([
                        dispatch('socket/close', null, {root: true}),
                        dispatch('removeUser'),
                        tagsViewMutations.delAllTagAndCache()
                    ])
                })
                .then(() => {
                    resolve()
                    window.location.reload()
                })
                .catch(error => reject(error))
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
