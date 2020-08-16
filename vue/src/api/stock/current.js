import {GetApi, PostApi} from "@/api/request"

export const search = new PostApi(`/stock/current/search`)

export const getDetail = new GetApi(`/stock/current/getDetail`, cids => ({params: {cids}}))

export const getDetailById = new GetApi(`/stock/current/getDetailById`, ids => ({params: {ids}}))
