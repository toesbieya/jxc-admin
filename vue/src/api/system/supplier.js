import {GetApi, PostApi} from "@/api/request"

export const getLimitRegion = new GetApi(`/system/supplier/getLimitRegion`)

export const search = new PostApi(`/system/supplier/search`)

export const add = new PostApi(`/system/supplier/add`)

export const update = new PostApi(`/system/supplier/update`)

export const del = new PostApi(`/system/supplier/del`)
