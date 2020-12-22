import axios from 'axios'
import {apiPrefix} from '@/config'
import {MessageBox, Notification} from 'element-ui'
import Message from '@ele/component/Message'
import store from '@/store'
import {isLogin} from '@/util/auth'

const instance = axios.create({
    baseURL: apiPrefix,
    // withCredentials: true, // send cookies when cross-domain requests
    timeout: 60000 // request timeout
})

instance.interceptors.request.use(
    config => {
        //登录状态下socket断连时，除登出外中断一切请求
        if (isLogin() && !store.state.websocket.online && config.url !== '/account/logout') {
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

instance.interceptors.response.use(
    response => {
        const res = response.data, {responseType = 'json'} = response.config

        //当返回类型非{status,data,msg}的接口请求时，不使用status来判断请求是否成功
        if (!('status' in res) || res.status === 200) {
            //当返回类型为json时，返回response.data
            return responseType === 'json' ? res : response
        }

        //服务器异常
        if (res.status === 500) {
            Message.error(res.msg || '操作失败')
            return Promise.reject(res.msg)
        }

        //未登录
        if (res.status === 401) {
            if (store.state.user.prepareLogout) return Promise.reject()
            return MessageBox.alert('请登录后重试', {
                type: 'warning',
                beforeClose: (action, instance, done) => {
                    store.dispatch('user/logout').then(done)
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

class Api {
    /**
     * 数据接口定义
     * @param url     请求url，不带参数
     * @param arg     对传入参数的处理方法，返回值将作为axios[get,post]的第二个参数
     * @param chain   形参为请求返回的promise
     * @param method  请求方法，小写，get、post...
     */
    constructor(url, arg, chain, method) {
        this.url = url
        this.arg = arg
        this.chain = chain
        this.method = method
    }

    request(...args) {
        const params = this.arg ? this.arg(...args) : undefined
        const promise = instance[this.method](this.url, params).catch(e => console.error(e))
        return this.chain ? this.chain(promise) : promise
    }
}

export class PostApi extends Api {
    constructor(url, arg, chain) {
        if (!arg) arg = data => data

        super(url, arg, chain, 'post')
    }
}

export class GetApi extends Api {
    constructor(url, arg, chain) {
        super(url, arg, chain, 'get')
    }
}

export default instance
