import {GetApi, PostApi} from "@/api/request"

export const getById = new GetApi('/doc/purchase/inbound/getById', id => ({params: {id}}))

export const getSubById = new GetApi('/doc/purchase/inbound/getSubById', id => ({params: {id}}))

export const search = new PostApi('/doc/purchase/inbound/search')

export const exportExcel = new PostApi('/doc/purchase/inbound/export')

export const add = new PostApi('/doc/purchase/inbound/add')

export const update = new PostApi('/doc/purchase/inbound/update')

export const commit = new PostApi('/doc/purchase/inbound/commit')

export const withdraw = new PostApi('/doc/purchase/inbound/withdraw')

export const pass = new PostApi('/doc/purchase/inbound/pass')

export const reject = new PostApi('/doc/purchase/inbound/reject')

export const del = new GetApi('/doc/purchase/inbound/del', id => ({params: {id}}))
