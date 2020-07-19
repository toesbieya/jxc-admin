import {isEmpty} from "@/utils"
import router from '@/router'
import store from '@/store'

export function closeCurrentPage(next) {
    return store
        .dispatch('tagsView/delView', router.currentRoute)
        .then(() => !isEmpty(next) && router.replace(next))
}
