import Message from '@ele/component/Message'
import {MessageBox} from 'element-ui'

export function elSuccess(message = '操作成功') {
    Message.success({
        message,
        dangerouslyUseHTMLString: true
    })
}

export function elConfirm(msg = '确认进行该操作？', ignoreReject = true) {
    const promise = MessageBox.confirm(msg, {
        type: 'warning',
        cancelButtonClass: 'is-plain'
    })

    if (ignoreReject) {
        promise.catch(() => ({}))
    }

    return promise
}
