import request from "@/config/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/department`

export function getDepartments(all = true) {
    return request.get(`${baseUrl}/get?all=${all}`).then(({data}) => data.data)
}

export function addDepartment(data) {
    return request.post(`${baseUrl}/add`, data).then(({data}) => data)
}

export function updateDepartment(data) {
    return request.post(`${baseUrl}/update`, data).then(({data}) => data)
}

export function delDepartment(data) {
    return request.post(`${baseUrl}/del`, data).then(({data}) => data)
}
