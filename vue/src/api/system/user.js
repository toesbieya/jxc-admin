import request from '@/config/request'

const baseUrl = '/system/user'

export function getUsers(data) {
    return request.post(baseUrl + '/search', data).then(({data}) => data.data)
}

export function getLoginHistory(data) {
    return request.post(baseUrl + '/getLoginHistory', data).then(({data}) => data.data)
}

export function getUserAction(data) {
    return request.post(baseUrl + '/getUserAction', data).then(({data}) => data.data)
}

export function kick(data) {
    return request.post(baseUrl + '/kick', data).then(({data}) => data)
}

export function addUser(data) {
    return request.post(baseUrl + '/add', data).then(({data}) => data)
}

export function updateUser(data) {
    return request.post(baseUrl + '/update', data).then(({data}) => data)
}

export function delUser(data) {
    return request.post(baseUrl + '/del', data).then(({data}) => data)
}

export function resetUserPwd(data) {
    return request.post(baseUrl + '/resetPwd', data).then(({data}) => data)
}

export function updateUserPwd(data) {
    return request.post(baseUrl + '/updatePwd', data).then(({data}) => data)
}

export function updateAvatar(key) {
    return request.get(baseUrl + '/updateAvatar?key=' + encodeURIComponent(key)).then(({data}) => ({...data, key}))
}

export function validate(pwd) {
    return request.get(baseUrl + '/validate?pwd=' + pwd).then(({data}) => data)
}
