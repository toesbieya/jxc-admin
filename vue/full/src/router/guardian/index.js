import nprogress from './nprogress'
import accessControl from './accessControl'

export default function (router) {
    nprogress(router)
    accessControl(router)
}
