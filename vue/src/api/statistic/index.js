import request from "@/api/request"

export const baseUrl = `/statistic`

export function getFourBlock() {
    return request.get(`${baseUrl}/getFourBlock`).then(({data}) => data.data)
}

export function getDailyProfitStat() {
    return request.get(`${baseUrl}/getDailyProfitStat`).then(({data}) => data.data)
}

export function getDailyFinishOrder() {
    return request.get(`${baseUrl}/getDailyFinishOrder`).then(({data}) => data.data)
}

export function getTotalProfitGoods() {
    return request.get(`${baseUrl}/getTotalProfitGoods`).then(({data}) => data.data)
}
