import {GetApi, PostApi} from "@/api/request"

export const getById = new GetApi('/doc/sell/order/getById', id => ({params: {id}}))

export const getSubById = new GetApi('/doc/sell/order/getSubById', id => ({params: {id}}))

export const search = new PostApi('/doc/sell/order/search')

export const exportExcel = new PostApi('/doc/sell/order/export')

export const add = new PostApi('/doc/sell/order/add')

export const update = new PostApi('/doc/sell/order/update')

export const commit = new PostApi('/doc/sell/order/commit')

export const withdraw = new PostApi('/doc/sell/order/withdraw')

export const pass = new PostApi('/doc/sell/order/pass')

export const reject = new PostApi('/doc/sell/order/reject')

export const del = new GetApi('/doc/sell/order/del', id => ({params: {id}}))
