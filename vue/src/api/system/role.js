import request from "@/api/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/role`

export function search(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function get() {
    return request.get(`${baseUrl}/get`).then(({data}) => data.data)
}

export function add(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function update(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function del(data) {
    return request.post(`${baseUrl}/del`, data).then(({data}) => data)
}
