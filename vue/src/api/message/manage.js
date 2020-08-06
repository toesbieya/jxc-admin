import request from "@/config/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/manage`

export function search(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function add(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function update(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function publish(data) {
    return request.post(`${baseUrl}/publish`, data).then(({data}) => data)
}

export function withdraw(data) {
    return request.post(`${baseUrl}/withdraw`, data).then(({data}) => data)
}

export function del(id, title) {
    return request.get(`${baseUrl}/del?id=${id}&title=${title}`).then(({data}) => data)
}
