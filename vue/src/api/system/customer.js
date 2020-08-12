import request from "@/api/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/customer`

export function getLimitRegion() {
    return request.get(`${baseUrl}/getLimitRegion`).then(({data}) => data.data)
}

export function getCustomers(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function addCustomer(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function updateCustomer(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function delCustomer(data) {
    return request.post(`${baseUrl}/del`, data).then(({data}) => data)
}
