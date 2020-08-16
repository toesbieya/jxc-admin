import {PostApi} from "@/api/request"

export const search = new PostApi(`/system/user/search`)

export const kick = new PostApi(`/system/user/kick`, null, p => p.then(({data}) => data))

export const add = new PostApi(`/system/user/add`, null, p => p.then(({data}) => data))

export const update = new PostApi(`/system/user/update`, null, p => p.then(({data}) => data))

export const del = new PostApi(`/system/user/del`, null, p => p.then(({data}) => data))

export const resetPwd = new PostApi(`/system/user/resetPwd`, null, p => p.then(({data}) => data))
