import request from "@/api/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/resource`

export function getAll() {
    return request.get(`${baseUrl}/getAll`).then(({data}) => data.data)
}

export function add(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function update(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function del(id) {
    return request.get(`${baseUrl}/del?id=${id}`).then(({data}) => data)
}
