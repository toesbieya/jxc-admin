import request from "@/config/request"

export const baseUrl = '/account'

export function login(data) {
    return request.post(`${baseUrl}/login`, data).then(({data}) => data.data)
}

export function logout() {
    return request.get(`${baseUrl}/logout`)
}

export function register(data) {
    return request.post(`${baseUrl}/register`, data)
}

export function updateUserPwd(data) {
    return request.post(`${baseUrl}/updatePwd`, data).then(({data}) => data)
}

export function updateAvatar(key) {
    return request.get(`${baseUrl}/updateAvatar?key=${encodeURIComponent(key)}`).then(({data}) => ({...data, key}))
}

export function validate(pwd) {
    return request.get(`${baseUrl}/validate?pwd=${pwd}`).then(({data}) => data)
}

export function checkName(name, id) {
    return request.get(`${baseUrl}/checkName?name=${name}&id=${id}`).then(({data}) => data)
}
