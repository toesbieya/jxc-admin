import {GetApi, PostApi} from "@/api/request"

export const getLimitRegion = new GetApi(`/system/supplier/getLimitRegion`)

export const search = new PostApi(`/system/supplier/search`)

export const add = new PostApi(`/system/supplier/add`, null, p => p.then(({data}) => data))

export const update = new PostApi(`/system/supplier/update`, null, p => p.then(({data}) => data))

export const del = new PostApi(`/system/supplier/del`, null, p => p.then(({data}) => data))
