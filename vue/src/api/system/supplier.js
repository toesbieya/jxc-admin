import request from "@/api/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/supplier`

export function getLimitRegion() {
    return request.get(`${baseUrl}/getLimitRegion`).then(({data}) => data.data)
}

export function getSuppliers(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function addSupplier(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function updateSupplier(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function delSupplier(data) {
    return request.post(`${baseUrl}/del`, data).then(({data}) => data)
}
