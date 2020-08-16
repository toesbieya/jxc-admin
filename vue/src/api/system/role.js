import {GetApi, PostApi} from "@/api/request"

export const baseUrl = `/system/role`

export const search = new PostApi(`/system/role/search`)

export const get = new GetApi(`/system/role/get`)

export const add = new PostApi(`/system/role/add`, null, p => p.then(({data}) => data))

export const update = new PostApi(`/system/role/update`, null, p => p.then(({data}) => data))

export const del = new PostApi(`/system/role/del`, null, p => p.then(({data}) => data))
