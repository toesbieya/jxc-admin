import {isEmpty} from "@/util"
import {unzip, zip} from "@/util/secret"

const sessionUserKey = 'GCC-SESS-USER'
const localPersonalSettingsKey = 'GCC-LOCAL-PERSONAL-SETTINGS'

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

export function getLocalPersonalSettings() {
    let obj = localStorage.getItem(localPersonalSettingsKey)
    if (isEmpty(obj)) return {}
    try {
        obj = JSON.parse(obj)
    }
    catch (e) {
        console.error('本地个性化设置数据解析失败！', e)
        removeLocalPersonalSettings()
        return {}
    }
    return obj
}

export function setLocalPersonalSettings(settings) {
    isEmpty(settings) ?
        removeLocalPersonalSettings() :
        localStorage.setItem(localPersonalSettingsKey, JSON.stringify(settings))
}

function removeUser() {
    sessionStorage.removeItem(sessionUserKey)
}

function removeLocalPersonalSettings() {
    localStorage.removeItem(localPersonalSettingsKey)
}
