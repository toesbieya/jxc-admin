import {MessageBox} from "element-ui"
import {isEmpty} from "@/util"
import {useMock, socketUrl} from '@/config'
import SocketIO from 'socket.io-client'
import {createMutations} from "@/store/util"

let websocket

const state = {
    online: true
}

const mutations = createMutations(state)

const actions = {
    init(context) {
        const {id, token} = context.rootState.user

        if (useMock || isEmpty(id, token)) return

        websocket = initSocket({id, token})

        bindDefaultEvent(websocket, context)
        bindCustomEvent(websocket, context)
    },

    close() {
        websocket && websocket.close()
    }
}

function initSocket({id, token}) {
    return new SocketIO(socketUrl, {query: {id, token}, transports: ['websocket']})
}

function bindDefaultEvent(socket, {state, commit, dispatch}) {
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

function bindCustomEvent(socket, {state, commit, dispatch, rootState}) {
    socket.on('logout', msg => {
        if (rootState.user.prepareLogout) return
        MessageBox.alert(msg || '你已被强制下线，请重新登录', {
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
