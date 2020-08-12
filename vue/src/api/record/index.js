import request from "@/api/request"

export const baseUrl = '/record'

export function searchLoginHistory(data) {
    return request.post(`${baseUrl}/searchLoginHistory`, data).then(({data}) => data.data)
}

export function searchUserAction(data) {
    return request.post(`${baseUrl}/searchUserAction`, data).then(({data}) => data.data)
}
