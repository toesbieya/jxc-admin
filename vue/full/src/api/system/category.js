import {GetApi, PostApi} from "@/api/request"

export const search = new PostApi(`/system/category/search`)

export const getAll = new GetApi(`/system/category/getAll`)

export const add = new PostApi(`/system/category/add`)

export const update = new PostApi(`/system/category/update`)

export const del = new PostApi(`/system/category/del`)
