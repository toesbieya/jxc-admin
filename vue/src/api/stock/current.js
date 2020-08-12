import request from "@/api/request"

export const baseUrl = '/stock/current'

export function search(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}

export function getDetail(cids) {
    return request.get(`${baseUrl}/getDetail?cids=${cids}`).then(({data}) => data.data)
}

export function getDetailById(ids) {
    return request.get(`${baseUrl}/getDetailById?ids=${ids}`).then(({data}) => data.data)
}
