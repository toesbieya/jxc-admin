import request from '@/config/request'

const baseUrl = '/stock/current'

export function search(data) {
    return request.post(baseUrl + '/search', data).then(({data}) => data.data)
}

export function getDetail(cid) {
    return request.get(baseUrl + '/getDetail?cid=' + cid).then(({data}) => data.data)
}

export function getDetailById(ids) {
    return request.get(baseUrl + '/getDetailById?ids=' + ids).then(({data}) => data.data)
}
