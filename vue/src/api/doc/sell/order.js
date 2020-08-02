import request from "@/config/request"
import BASE from '../baseUrl'

export const baseUrl = `${BASE}/sell/order`

export function getById(id) {
    return request.get(`${baseUrl}/getById?id=${id}`).then(({data}) => data.data)
}

export function getSubById(id) {
    return request.get(`${baseUrl}/getSubById?id=${id}`).then(({data}) => data.data)
}

export function search(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function add(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function update(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function commit(data) {
    return request.post(`${baseUrl}/commit`, data).then(({data}) => data)
}

export function withdraw(data) {
    return request.post(`${baseUrl}/withdraw`, data).then(({data}) => data)
}

export function pass(data) {
    return request.post(`${baseUrl}/pass?`, data).then(({data}) => data)
}

export function reject(data) {
    return request.post(`${baseUrl}/reject`, data).then(({data}) => data)
}

export function del(id) {
    return request.get(`${baseUrl}/del?id=${id}`).then(({data}) => data)
}
