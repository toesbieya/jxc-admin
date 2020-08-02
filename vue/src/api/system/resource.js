import request from "@/config/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/resource`

export function getAllResources() {
    return request.get(`${baseUrl}/getAll`).then(({data}) => data.data)
}

export function updateResource(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}
