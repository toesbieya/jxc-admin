import {GetApi, PostApi} from "@/api/request"

export const getById = new GetApi('/doc/purchase/order/getById', id => ({params: {id}}))

export const getSubById = new GetApi('/doc/purchase/order/getSubById', id => ({params: {id}}))

export const search = new PostApi('/doc/purchase/order/search')

export const exportExcel = new PostApi('/doc/purchase/order/export')

export const add = new PostApi('/doc/purchase/order/add')

export const update = new PostApi('/doc/purchase/order/update')

export const commit = new PostApi('/doc/purchase/order/commit')

export const withdraw = new PostApi('/doc/purchase/order/withdraw')

export const pass = new PostApi('/doc/purchase/order/pass')

export const reject = new PostApi('/doc/purchase/order/reject')

export const del = new GetApi('/doc/purchase/order/del', id => ({params: {id}}))
