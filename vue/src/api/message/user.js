import {GetApi, PostApi} from "@/api/request"

export const search = new PostApi(`/message/user/search`)

export const read = new GetApi(`/message/user/read`, id => ({params: {id}}))

export const readAll = new GetApi(`/message/user/readAll`)
