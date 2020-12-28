import {storage} from "@/config"
import {isEmpty} from "@/util"
import {unzip, zip} from "@/util/secret"

const {keyPrefix, zip: defaultUseZip} = storage
const sessionUserKey = 'SESS-USER'
const localPersonalSettingsKey = 'LOCAL-PERSONAL-SETTINGS'
const sessionTagsViewKey = 'SESS-TAGS-VIEW'

/**
 * 读取本地存储
 * @param key {string}        键名，自动加上统一前缀
 * @param storage {Storage}   window.sessionStorage或window.localStorage
 * @param useZip {boolean}    是否启用了压缩
 * @return {*|undefined}
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
 * @param key {string}        键名，自动加上统一前缀
 * @param obj                 需要写入本地存储的js对象
 * @param storage {Storage}   window.sessionStorage或window.localStorage
 * @param useZip {boolean}    是否启用了压缩
 */
export function set(key, obj, storage = window.sessionStorage, useZip = defaultUseZip) {
    const item = useZip ? zip(JSON.stringify(obj)) : JSON.stringify(obj)
    storage.setItem(`${keyPrefix}${key}`, item)
}

/**
 * 根据指定键名，移除本地存储对应项
 * @param key {string}       键名，自动加上统一前缀
 * @param storage {Storage}  window.sessionStorage或window.localStorage
 */
export function remove(key, storage = window.sessionStorage) {
    storage.removeItem(`${keyPrefix}${key}`)
}

/**
 * 获取用户信息，获取失败时会清除用户信息并返回一个空对象
 *
 * @return {object}
 */
export function getUser() {
    let user = get(sessionUserKey)
    if (isEmpty(user)) {
        user = {}
        removeUser()
    }
    return user
}

/**
 * 将用户信息持久化到sessionStorage，传入空值时将清除用户信息
 *
 * @param user
 */
export function setUser(user) {
    isEmpty(user) ? removeUser() : set(sessionUserKey, user)
}

/**
 * 获取用户个性设置，获取失败时会清除设置信息并返回一个空对象
 *
 * @return {object}
 */
export function getLocalPersonalSettings() {
    let settings = get(localPersonalSettingsKey, window.localStorage)
    if (isEmpty(settings)) {
        settings = {}
        removeLocalPersonalSettings()
    }
    return settings
}

/**
 * 将用户个性设置持久化到localStorage，传入空值时将清除设置信息
 *
 * @param settings
 */
export function setLocalPersonalSettings(settings) {
    isEmpty(settings)
        ? removeLocalPersonalSettings()
        : set(localPersonalSettingsKey, settings, window.localStorage)
}

/**
 * 获取持久化后的页签，获取失败时会清除持久化数据并返回一个空数组
 *
 * @return {array}
 */
export function getTagsView() {
    let tagsView = get(sessionTagsViewKey)
    if (isEmpty(tagsView)) {
        tagsView = []
        removeTagsView()
    }
    return tagsView
}

/**
 * 将页签持久化到sessionStorage，传入空值时将清除页签信息
 *
 * @param tagsView
 */
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
