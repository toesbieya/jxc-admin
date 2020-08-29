import {title} from "@/config"

const beforeEach = (to, from, next) => {
    const {path, meta} = to

    if (!path.startsWith('/redirect')) {
        if (typeof meta.dynamicTitle === 'function') {
            meta.title = meta.dynamicTitle(to, from)
        }

        const pageTitle = meta.title
        document.title = pageTitle ? `${pageTitle} - ${title}` : title
    }

    next()
}

export default function (router) {
    router.beforeEach(beforeEach)
}
