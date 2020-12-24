import Message from '@ele/component/Message'
import {MessageBox} from 'element-ui'
import {isEmpty} from "@/util"

export function elError(msg = '操作失败') {
    Message.error(msg)
}

export function elSuccess(message = '操作成功') {
    Message.success({
        message,
        dangerouslyUseHTMLString: true
    })
}

export function elAlert(msg, callback = () => ({})) {
    return MessageBox.alert(msg, {
        type: 'warning',
        cancelButtonClass: 'is-plain',
        callback
    })
}

export function elConfirm(msg = '确认进行该操作？', ignoreReject = true) {
    return new Promise(((resolve, reject) => {
        MessageBox.confirm(msg, {
            type: 'warning',
            cancelButtonClass: 'is-plain'
        })
            .then(resolve, ignoreReject ? () => ({}) : reject)
    }))
}

export function elPrompt(msg) {
    return new Promise(resolve => {
        MessageBox.prompt(msg, {
            type: 'warning',
            cancelButtonClass: 'is-plain',
            inputType: 'textarea',
            inputValidator: str => {
                if (isEmpty(str)) return '请输入内容'
                if (str.length > 200) return '长度最多200'
                return true
            }
        })
            .then(({value}) => resolve(value))
            .catch(() => ({}))
    })
}
