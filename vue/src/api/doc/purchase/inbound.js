import {GetApi, PostApi} from "@/api/request"

export const baseUrl = `/doc/purchase/inbound`

export const getById = new GetApi(`${baseUrl}/getById`, id => ({params: {id}}))

export const getSubById = new GetApi(`${baseUrl}/getSubById`, id => ({params: {id}}))

export const search = new PostApi(`${baseUrl}/search`)

export const add = new PostApi(`${baseUrl}/add`, null, p => p.then(({data}) => data))

export const update = new PostApi(`${baseUrl}/update`, null, p => p.then(({data}) => data))

export const commit = new PostApi(`${baseUrl}/commit`, null, p => p.then(({data}) => data))

export const withdraw = new PostApi(`${baseUrl}/withdraw`, null, p => p.then(({data}) => data))

export const pass = new PostApi(`${baseUrl}/pass`, null, p => p.then(({data}) => data))

export const reject = new PostApi(`${baseUrl}/reject`, null, p => p.then(({data}) => data))

export const del = new GetApi(`${baseUrl}/del`, id => ({params: {id}}), p => p.then(({data}) => data))
