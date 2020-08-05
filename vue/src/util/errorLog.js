import Vue from 'vue'
import {errorLog} from '@/config'

if (errorLog) {
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
