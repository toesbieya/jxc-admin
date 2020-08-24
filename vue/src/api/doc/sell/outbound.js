import {GetApi, PostApi} from "@/api/request"

export const baseUrl = `/doc/sell/outbound`

export const getById = new GetApi(`${baseUrl}/getById`, id => ({params: {id}}))

export const getSubById = new GetApi(`${baseUrl}/getSubById`, id => ({params: {id}}))

export const search = new PostApi(`${baseUrl}/search`)

export const add = new PostApi(`${baseUrl}/add`)

export const update = new PostApi(`${baseUrl}/update`)

export const commit = new PostApi(`${baseUrl}/commit`)

export const withdraw = new PostApi(`${baseUrl}/withdraw`)

export const pass = new PostApi(`${baseUrl}/pass`)

export const reject = new PostApi(`${baseUrl}/reject`)

export const del = new GetApi(`${baseUrl}/del`, id => ({params: {id}}))
