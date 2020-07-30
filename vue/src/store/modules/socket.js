import {MessageBox} from "element-ui"
import {isEmpty} from "@/utils"
import {useMock, socketUrl} from '@/config'
import SocketIO from 'socket.io-client'
import {createMutations} from "@/utils"

let socket

const state = {
    online: true
}

const mutations = createMutations(state)

const actions = {
    init(context, user) {
        if (useMock || isEmpty(user, user.id, user.token)) return

        socket = initSocket(user)

        defaultEventBind(socket, context)
        customEventBind(socket, context)
    },

    close() {
        return new Promise(resolve => {
            if (!socket) return resolve()
            socket.close()
            resolve()
        })
    }
}

function initSocket({id, token}) {
    return new SocketIO(socketUrl, {query: {id, token}})
}

function defaultEventBind(socket, {state, commit, dispatch}) {
    socket.on('connect', () => {
        commit('online', true)
        console.log('socket连接成功')
    })

    socket.on('disconnect', (reason) => {
        commit('online', false)
        if (reason === 'io server disconnect') {
            return console.log('服务端关闭了socket连接')
        }
        console.log('socket失去连接')
    })

    socket.on('reconnecting', (attemptNumber) => {
        commit('online', false)
        console.log(`socket第${attemptNumber}次重连中...`)
    })

    socket.on('reconnect', (attemptNumber) => {
        commit('online', true)
        console.log(`socket第${attemptNumber}次重连成功`)
    })

    socket.on('reconnect_failed', () => {
        // ...
    })

    socket.on('reconnect_error', (error) => {
        // ...
    })

    socket.on('error', (error) => {
        // ...
    })
}

function customEventBind(socket, {state, commit, dispatch, rootState}) {
    socket.on('logout', msg => {
        if (rootState.user.prepare_logout) return
        MessageBox.alert(msg || '你已被强制下线，请重新登陆', {
            type: 'warning',
            beforeClose: (action, instance, done) => {
                dispatch('user/logout', null, {root: true}).finally(done)
            }
        })
    })

    socket.on('new-message', () => {
        dispatch('message/refresh', null, {root: true})
    })
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
