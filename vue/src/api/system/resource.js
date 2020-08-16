import {GetApi, PostApi} from "@/api/request"

export const getAll = new GetApi(`/system/resource/getAll`)

export const add = new PostApi(`/system/resource/add`, null, p => p.then(({data}) => data))

export const update = new PostApi(`/system/resource/update`, null, p => p.then(({data}) => data))

export const del = new GetApi(`/system/resource/del`, id => ({params: {id}}), p => p.then(({data}) => data))
