import request from "@/config/request"

export const baseUrl = `/system/role`

export function searchRoles(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function getRoles() {
    return request.get(`${baseUrl}/get`).then(({data}) => data.data)
}

export function addRole(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function updateRole(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function delRole(data) {
    return request.post(`${baseUrl}/del`, data).then(({data}) => data)
}
