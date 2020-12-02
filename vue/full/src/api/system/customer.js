import {GetApi, PostApi} from "@/api/request"

export const getLimitRegion = new GetApi(`/system/customer/getLimitRegion`)

export const search = new PostApi(`/system/customer/search`)

export const add = new PostApi(`/system/customer/add`)

export const update = new PostApi(`/system/customer/update`)

export const del = new PostApi(`/system/customer/del`)
