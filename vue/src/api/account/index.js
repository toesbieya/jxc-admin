import {GetApi, PostApi} from "@/api/request"

export const login = new PostApi('/account/login')

export const logout = new GetApi('/account/logout')

export const register = new PostApi('/account/register')

export const updateUserPwd = new PostApi('/account/updatePwd')

export const updateAvatar = new GetApi(
    '/account/updateAvatar',
    key => ({params: {key: encodeURIComponent(key)}})
)

export const validate = new GetApi('/account/validate', pwd => ({params: {pwd}}))

export const checkLoginName = new GetApi(
    '/account/checkLoginName',
    (name, id) => ({params: {name, id}})
)

export const checkNickName = new GetApi(
    '/account/checkNickName',
    (name, id) => ({params: {name, id}})
)
