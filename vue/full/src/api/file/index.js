import {GetApi, PostApi} from "@/api/request"

export const getToken = new GetApi(
    '/file/getToken',
    null,
    promise => promise.then(({data}) => data)
)

export const deleteUpload = new GetApi('/file/delete', url => ({params: {url: encodeURIComponent(url)}}))

export const deleteUploadBatch = new PostApi('/file/deleteBatch')
