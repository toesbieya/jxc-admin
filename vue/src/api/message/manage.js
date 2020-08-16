import {GetApi, PostApi} from "@/api/request"

export const search = new PostApi(`/message/manage/search`)

export const add = new PostApi(`/message/manage/add`, null, p => p.then(({data}) => data))

export const update = new PostApi(`/message/manage/update`, null, p => p.then(({data}) => data))

export const publish = new PostApi(`/message/manage/publish`, null, p => p.then(({data}) => data))

export const withdraw = new PostApi(`/message/manage/withdraw`, null, p => p.then(({data}) => data))

export const del = new GetApi(`/message/manage/del`, (id, title) => ({params: {id, title}}))
