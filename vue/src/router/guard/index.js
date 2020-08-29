import nprogress from './nprogress'
import avoidReuse from './avoidReuse'
import setInfo from './setInfo'
import security from './security'
import iframe from './iframe'

export default function (router) {
    nprogress(router)
    avoidReuse(router)
    setInfo(router)
    security(router)
    iframe(router)
}
