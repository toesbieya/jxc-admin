import request from '@/config/request'

const baseUrl = '/system/department'

export function getDepartments() {
    return request.get(baseUrl + '/get').then(({data}) => data.data)
}

export function getAllDepartments() {
    return request.get(baseUrl + '/getAll').then(({data}) => data.data)
}

export function addDepartment(data) {
    return request.post(baseUrl + '/add', data).then(({data}) => data)
}

export function updateDepartment(data) {
    return request.post(baseUrl + '/update', data).then(({data}) => data)
}

export function delDepartment(data) {
    return request.post(baseUrl + '/del', data).then(({data}) => data)
}
