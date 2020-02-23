import request from '@/config/request'

const baseUrl = '/document/history'

export function getDocumentHistoryByPid(pid) {
    return request.get(baseUrl + '/get?pid=' + pid).then(({data}) => data.data)
}
