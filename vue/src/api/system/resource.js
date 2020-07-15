import request from "@/config/request"

export const baseUrl = `/system/resource`

export function getResources() {
    return request.get(`${baseUrl}/get`).then(({data}) => data.data)
}

export function getAllResources() {
    return request.get(`${baseUrl}/getAll`).then(({data}) => data.data)
}

export function updateResource(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}
