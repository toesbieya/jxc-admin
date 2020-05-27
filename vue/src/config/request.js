import axios from 'axios'
import {apiPrefix} from '@/config'
import {MessageBox, Notification} from 'element-ui'
import Message from '@/components/Message'
import store from '@/store'

const service = axios.create({
    baseURL: apiPrefix,
    // withCredentials: true, // send cookies when cross-domain requests
    timeout: 60000 // request timeout
})

service.interceptors.request.use(
    config => {
        //登录状态下socket断连时，除登出外中断一切请求
        if (store.state.user.id && !store.state.socket.online && config.url !== '/logout') {
            Message.error('请等待与服务器重新连接')
            return Promise.reject('')
        }

        //header添加token
        if (store.state.user.token) {
            config.headers['X-Token'] = store.state.user.token
        }
        return config
    },
    error => Promise.reject(error)
)

service.interceptors.response.use(
    response => {
        const res = response.data

        //当返回类型非{status,data,msg}的接口请求时，不使用status来判断请求是否成功
        if (!('status' in res) || res.status === 200) {
            return response
        }

        //服务器异常
        if (res.status === 500) {
            Message.error(res.msg || '操作失败')
            return Promise.reject(res.msg)
        }

        //未登录
        if (res.status === 401) {
            if (store.state.user.prepare_logout) return Promise.reject()
            return MessageBox.alert('请登录后重试', {
                type: 'warning',
                beforeClose: (action, instance, done) => {
                    store.dispatch('user/logout').then(() => done())
                }
            })
        }

        //没有权限
        if (res.status === 403) {
            Message.error(res.msg || '没有权限进行该操作')
            return Promise.reject(res.msg)
        }

        //其他错误
        Message.error(res.msg || '接口有误')
        return Promise.reject(res)
    },
    error => {
        if (axios.isCancel(error)) return
        error && Notification.error({
            title: '错误',
            message: '请求错误，请稍后重试'
        })
        return Promise.reject(error)
    }
)

export default service
