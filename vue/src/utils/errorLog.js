import Vue from 'vue'
import {isString} from '@/utils/validate'
import settings from '@/config'

// errorLog:'production' | ['production', 'development']
const {errorLog} = settings

function checkNeed() {
    const env = process.env.NODE_ENV
    if (isString(errorLog)) {
        return env === errorLog
    }
    if (Array.isArray(errorLog)) {
        return errorLog.includes(env)
    }
    return false
}

if (checkNeed()) {
    Vue.config.errorHandler = function (err, vm, info, a) {
        //忽略image-viewer错误
        if (err.message === `Cannot read property 'complete' of undefined`) return
        /*store.commit('errorLog/ADD_ERROR_LOG', {
            err,
            info,
            url: window.location.href
        })*/
        console.error('这是vue捕捉到的异常', err, info)
    }
}
