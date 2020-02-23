import request from '@/config/request'

const baseUrl = '/system/category'

export function search(data) {
    return request.post(baseUrl + '/search', data).then(({data}) => data.data)
}

export function getAllCategories() {
    return request.get(baseUrl + '/getAll').then(({data}) => data.data)
}

export function addCategory(data) {
    return request.post(baseUrl + '/add', data).then(({data}) => data)
}

export function updateCategory(data) {
    return request.post(baseUrl + '/update', data).then(({data}) => data)
}

export function delCategory(data) {
    return request.post(baseUrl + '/del', data).then(({data}) => data)
}
