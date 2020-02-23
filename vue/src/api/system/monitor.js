import request from '@/config/request'

const baseUrl = '/system/monitor'

export function getMonitorInfo() {
    return request.get(baseUrl + '/get').then(({data}) => data.data)
}
