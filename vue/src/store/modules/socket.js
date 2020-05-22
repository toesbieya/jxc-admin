import {MessageBox} from "element-ui"
import {socketUrl} from '@/config'
import SocketIO from 'socket.io-client'
import {createMutations} from "@/utils"

const state = {
    socket: null,
    online: true
}

const mutations = createMutations(state)

const actions = {
    init({state, commit, dispatch, rootState}, user) {
        if (!user || !user.id || !user.session_id) return

        const socket = initSocket(user)

        defaultEventBind(socket, {state, commit, dispatch})

        socket.on('logout', msg => {
            if (rootState.user.prepare_logout) return
            return MessageBox.alert(msg || '你已被强制下线，请重新登陆', {
                type: 'warning',
                beforeClose: (action, instance, done) => {
                    dispatch('user/logout', null, {root: true})
                        .then(() => {
                            done()
                            location.reload()
                        })
                }
            })
        })

        commit('socket', socket)
    },
    close({state, commit}) {
        return new Promise(resolve => {
            if (!state.socket) return resolve()
            state.socket.close()
            commit('socket', null)
            resolve()
        })
    }
}

function initSocket({id, session_id}) {
    return new SocketIO(socketUrl, {
        query: {id, session_id},
        transports: ['websocket']
    })
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

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
