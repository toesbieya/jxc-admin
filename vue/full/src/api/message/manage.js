import {GetApi, PostApi} from "@/api/request"

export const search = new PostApi(`/message/manage/search`)

export const add = new PostApi(`/message/manage/add`)

export const update = new PostApi(`/message/manage/update`)

export const publish = new PostApi(`/message/manage/publish`)

export const withdraw = new PostApi(`/message/manage/withdraw`)

export const del = new GetApi(`/message/manage/del`, (id, title) => ({params: {id, title}}))
