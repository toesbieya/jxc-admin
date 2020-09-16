import {GetApi, PostApi} from "@/api/request"

export const getById = new GetApi('/doc/sell/outbound/getById', id => ({params: {id}}))

export const getSubById = new GetApi('/doc/sell/outbound/getSubById', id => ({params: {id}}))

export const search = new PostApi('/doc/sell/outbound/search')

export const exportExcel = new PostApi('/doc/sell/outbound/export')

export const add = new PostApi('/doc/sell/outbound/add')

export const update = new PostApi('/doc/sell/outbound/update')

export const commit = new PostApi('/doc/sell/outbound/commit')

export const withdraw = new PostApi('/doc/sell/outbound/withdraw')

export const pass = new PostApi('/doc/sell/outbound/pass')

export const reject = new PostApi('/doc/sell/outbound/reject')

export const del = new GetApi('/doc/sell/outbound/del', id => ({params: {id}}))
