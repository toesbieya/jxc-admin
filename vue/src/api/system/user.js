import request from "@/api/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/user`

export function getUsers(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function kick(data) {
    return request.post(`${baseUrl}/kick`, data).then(({data}) => data)
}

export function addUser(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function updateUser(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function delUser(data) {
    return request.post(`${baseUrl}/del`, data).then(({data}) => data)
}

export function resetUserPwd(data) {
    return request.post(`${baseUrl}/resetPwd`, data).then(({data}) => data)
}
