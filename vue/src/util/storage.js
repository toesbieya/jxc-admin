import {storage} from "@/config"
import {isEmpty} from "@/util"
import {unzip, zip} from "@/util/secret"

const {keyPrefix, zip: defaultUseZip} = storage

/**
 * @desc 读取本地存储
 * @param key                键名，自动加上统一前缀
 * @param storage            window.sessionStorage或window.localStorage
 * @param useZip             是否启用了压缩
 * @returns {any|undefined}
 */
export function get(key, storage = window.sessionStorage, useZip = defaultUseZip) {
    let obj = storage.getItem(`${keyPrefix}${key}`)
    if (isEmpty(obj)) return undefined
    try {
        obj = JSON.parse(useZip ? unzip(obj) : obj)
    }
    catch (e) {
        console.error(`获取本地存储[${key}]失败`, e)
        obj = undefined
    }
    return obj
}

/**
 * 写入本地存储
 * @param key                键名，自动加上统一前缀
 * @param obj                需要写入本地存储的js对象
 * @param storage            window.sessionStorage或window.localStorage
 * @param useZip             是否启用了压缩
 */
export function set(key, obj, storage = window.sessionStorage, useZip = defaultUseZip) {
    const item = useZip ? zip(JSON.stringify(obj)) : JSON.stringify(obj)
    storage.setItem(`${keyPrefix}${key}`, item)
}

/**
 * @desc 判断本地存储中是否存在指定键名
 * @param key                键名，自动加上统一前缀
 * @param storage            window.sessionStorage或window.localStorage
 * @returns {boolean}        存在则返回true
 */
export function exist(key, storage = window.sessionStorage) {
    return !isEmpty(storage.getItem(`${keyPrefix}${key}`))
}

/**
 * @desc 根据指定键名，移除本地存储对应项
 * @param key                键名，自动加上统一前缀
 * @param storage            window.sessionStorage或window.localStorage
 */
export function remove(key, storage = window.sessionStorage) {
    storage.removeItem(`${keyPrefix}${key}`)
}

const sessionUserKey = 'SESS-USER'
const localPersonalSettingsKey = 'LOCAL-PERSONAL-SETTINGS'
const sessionTagsViewKey = 'SESS-TAGS-VIEW'

export function isUserExist() {
    return exist(sessionUserKey)
}

export function getUser() {
    let user = get(sessionUserKey)
    if (isEmpty(user)) {
        user = {}
        removeUser()
    }
    return user
}

export function setUser(user) {
    isEmpty(user) ? removeUser() : set(sessionUserKey, user)
}

export function getLocalPersonalSettings() {
    let settings = get(localPersonalSettingsKey, window.localStorage)
    if (isEmpty(settings)) {
        settings = {}
        removeLocalPersonalSettings()
    }
    return settings
}

export function setLocalPersonalSettings(settings) {
    isEmpty(settings)
        ? removeLocalPersonalSettings()
        : set(localPersonalSettingsKey, settings, window.localStorage)
}

export function getTagsView() {
    let tagsView = get(sessionTagsViewKey)
    if (isEmpty(tagsView)) {
        tagsView = []
        removeTagsView()
    }
    return tagsView
}

export function setTagsView(tagsView) {
    isEmpty(tagsView) ? removeTagsView() : set(sessionTagsViewKey, tagsView)
}

function removeUser() {
    remove(sessionUserKey)
}

function removeLocalPersonalSettings() {
    remove(localPersonalSettingsKey, window.localStorage)
}

function removeTagsView() {
    remove(sessionTagsViewKey)
}
