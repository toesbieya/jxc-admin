import {GetApi, PostApi} from "@/api/request"

export const login = new PostApi(`/account/login`)

export const logout = new GetApi(`/account/logout`)

export const register = new PostApi(`/account/register`)

export const updateUserPwd = new PostApi(`/account/updatePwd`)

export const updateAvatar = new GetApi(
    `/account/updateAvatar`,
    key => ({params: {key: encodeURIComponent(key)}}),
    p => p.then(({data}) => data)
)

export const validate = new GetApi(`/account/validate`, pwd => ({params: {pwd}}))

export const checkName = new GetApi(
    `/account/checkName`,
    (name, id) => ({params: {name, id}}),
    p => p.then(({data}) => data)
)
