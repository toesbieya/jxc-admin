import {login, logout} from '@/api/account'
import {createMutations, isEmpty} from "@/utils"
import {autoCompleteUrl} from "@/utils/file"
import {getUser, setUser} from "@/utils/storage"

//刷新时从本地存储中获取用户信息
const user = getUser()

const state = {
    //是否在退出的过程中，避免重复弹框
    prepare_logout: '',

    /*用户基本信息*/
    id: !isEmpty(user.id) ? user.id : null,
    admin: !isEmpty(user.admin) ? user.admin : 0,
    token: !isEmpty(user.token) ? user.token : '',
    name: !isEmpty(user.name) ? user.name : '',
    avatar: !isEmpty(user.avatar) ? user.avatar : '',
    role_name: !isEmpty(user.role_name) ? user.role_name : '',
    resources: !isEmpty(user.resources) ? user.resources : {},
    session_id: !isEmpty(user.session_id) ? user.session_id : ''
}

const mutations = createMutations(state, true)

const actions = {
    login({commit, dispatch}, userInfo) {
        const {username, password} = userInfo
        return new Promise((resolve, reject) => {
            login({username: username.trim(), password})
                .then(user => {
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
                .then(() => dispatch('socket/close', null, {root: true}))
                .then(() => {
                    dispatch('removeUser')
                    dispatch('tagsView/delAllViews', null, {root: true})
                    commit('resource/hasInitRoutes', false, {root: true})
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
