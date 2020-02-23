import {sessionUserKey} from '@/config'
import {isEmpty, unzip, zip} from "@/utils"

export function isUserExist() {
    return !isEmpty(sessionStorage.getItem(sessionUserKey))
}

export function getUser() {
    let obj = sessionStorage.getItem(sessionUserKey)
    if (isEmpty(obj)) return {}
    try {
        obj = JSON.parse(unzip(obj))
    }
    catch (e) {
        console.error('用户数据异常！', e)
        obj = {}
        removeUser()
    }
    return obj
}

export function setUser(user) {
    if (isEmpty(user)) return removeUser()
    sessionStorage.setItem(sessionUserKey, zip(JSON.stringify(user)))
}

function removeUser() {
    sessionStorage.removeItem(sessionUserKey)
}
