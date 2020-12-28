import {createMutations} from "@/store/util"
import {emptyOrDefault} from "@/util"
import {autoCompleteUrl} from "@/util/file" //此处存在循环引用？
import {getUser, setUser} from "@/util/storage"
import {tagsViewMutations} from "el-admin-layout"
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

                return dispatch('websocket/init', undefined, {root: true})
            })
    },

    logout({commit, state, dispatch}) {
        if (state.prepareLogout) return Promise.reject()

        commit('prepareLogout', true)

        return logout
            .request(state.token)
            .then(() => Promise.all([
                dispatch('websocket/close', null, {root: true}),
                dispatch('removeUser'),
                //因为可能开启了页签的持久化功能，所以退出登陆时通过清空store来清空持久化数据
                tagsViewMutations.delAllTagAndCache()
            ]))
            .then(() => window.location.reload())
            .finally(() => commit('prepareLogout', false))
    },

    //刷新本地存储中保存的用户数据
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
