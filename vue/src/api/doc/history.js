import request from "@/api/request"
import BASE from './baseUrl'

export const baseUrl = `${BASE}/history`

export function searchHistory(data) {
    return request.post(`${baseUrl}/search`, data).then(({data}) => data.data)
}
