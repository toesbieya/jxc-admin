import {GetApi, PostApi} from "@/api/request"

export const get = new GetApi(`/system/department/get`, (all = true) => ({params: {all}}))

export const add = new PostApi(`/system/department/add`)

export const update = new PostApi(`/system/department/update`)

export const del = new PostApi(`/system/department/del`)
