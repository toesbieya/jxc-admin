import {GetApi, PostApi} from "@/api/request"

export const search = new PostApi(`/system/role/search`)

export const get = new GetApi(`/system/role/get`)

export const add = new PostApi(`/system/role/add`)

export const update = new PostApi(`/system/role/update`)

export const del = new PostApi(`/system/role/del`)
