import {PostApi} from "@/api/request"

export const searchLoginHistory = new PostApi(`/record/searchLoginHistory`)

export const searchUserAction = new PostApi(`/record/searchUserAction`)
