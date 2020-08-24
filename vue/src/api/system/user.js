import {PostApi} from "@/api/request"

export const search = new PostApi(`/system/user/search`)

export const kick = new PostApi(`/system/user/kick`)

export const add = new PostApi(`/system/user/add`)

export const update = new PostApi(`/system/user/update`)

export const del = new PostApi(`/system/user/del`)

export const resetPwd = new PostApi(`/system/user/resetPwd`)
