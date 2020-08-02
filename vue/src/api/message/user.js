import request from "@/config/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/user`

export function search(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function read(id) {
    return request.get(`${baseUrl}/read?id=${id}`).then(({data}) => data)
}

export function readAll() {
    return request.get(`${baseUrl}/readAll`).then(({data}) => data)
}
