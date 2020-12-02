import nprogress from './nprogress'
import avoidReuse from './avoidReuse'
import setTitle from './setTitle'
import accessControl from './accessControl'
import iframe from './iframe'

export default function (router) {
    nprogress(router)
    avoidReuse(router)
    accessControl(router)
    setTitle(router)
    iframe(router)
}
