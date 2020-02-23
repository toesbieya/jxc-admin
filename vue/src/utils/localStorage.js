import {localPersonalSettingsKey, localResourceKey} from '@/config'
import {isEmpty, unzip, zip} from "@/utils"

export function getLocalResource() {
    let obj = localStorage.getItem(localResourceKey)
    if (isEmpty(obj)) return []
    try {
        obj = JSON.parse(unzip(obj))
    }
    catch (e) {
        console.error('权限数据异常！', e)
        removeLocalResource()
        return []
    }
    return obj
}

export function setLocalResource(resource) {
    isEmpty(resource) ?
        removeLocalResource() :
        localStorage.setItem(localResourceKey, zip(JSON.stringify(resource)))
}

export function getLocalPersonalSettings() {
    let obj = localStorage.getItem(localPersonalSettingsKey)
    if (isEmpty(obj)) return {}
    try {
        obj = JSON.parse(obj)
    }
    catch (e) {
        console.error('个性化设置数据异常！', e)
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

function removeLocalResource() {
    localStorage.removeItem(localResourceKey)
}

function removeLocalPersonalSettings() {
    localStorage.removeItem(localPersonalSettingsKey)
}
