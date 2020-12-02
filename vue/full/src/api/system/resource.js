import {GetApi, PostApi} from "@/api/request"

export const getAll = new GetApi(`/system/resource/getAll`)

export const add = new PostApi(`/system/resource/add`)

export const update = new PostApi(`/system/resource/update`)

export const del = new GetApi(`/system/resource/del`, id => ({params: {id}}))
