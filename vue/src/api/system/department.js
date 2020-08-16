import {GetApi, PostApi} from "@/api/request"

export const get = new GetApi(`/system/department/get`, (all = true) => ({params: {all}}))

export const add = new PostApi(`/system/department/add`, null, p => p.then(({data}) => data))

export const update = new PostApi(`/system/department/update`, null, p => p.then(({data}) => data))

export const del = new PostApi(`/system/department/del`, null, p => p.then(({data}) => data))
