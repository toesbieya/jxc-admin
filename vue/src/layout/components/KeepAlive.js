import {isEmpty} from "@/utils"

const KEY = '_routerViewKey'

function getFirstComponentChild(children) {
    if (Array.isArray(children)) {
        for (const c of children) {
            if (!isEmpty(c) && (!isEmpty(c.componentOptions) || c.isComment && c.asyncFactory)) {
                return c
            }
        }
    }
}

function getComponentName(opts) {
    return opts && (opts.Ctor.options.name || opts.tag)
}

function pruneCacheEntry(cache, key, current) {
    const cached = cache[key]
    if (cached && (!current || cached.tag !== current.tag)) {
        cached.componentInstance && cached.componentInstance.$destroy()
    }
    delete cache[key]
}

function getCacheKey(route, componentOptions) {
    if (KEY in componentOptions) return componentOptions[KEY]

    const {path, meta} = route
    const key = meta && meta.isDetailPage ? path : getComponentName(componentOptions)

    componentOptions[KEY] = key

    return key
}

export default {
    name: 'keep-router-view-alive',
    abstract: true,

    props: {include: Array},

    watch: {
        include: {
            deep: true,
            handler(val) {
                const {cache, $route, _vnode} = this
                for (const key of Object.keys(cache)) {
                    const cachedNode = cache[key]
                    const name = getCacheKey($route, cachedNode.componentOptions)
                    if (name && !val.includes(name)) {
                        pruneCacheEntry(cache, key, _vnode)
                    }
                }
            }
        }
    },

    created() {
        this.cache = Object.create(null)
    },

    beforeDestroy() {
        for (const key of Object.keys(this.cache)) {
            pruneCacheEntry(this.cache, key)
        }
    },

    render() {
        const slot = this.$slots.default
        const vnode = getFirstComponentChild(slot)
        let componentOptions = vnode && vnode.componentOptions

        if (componentOptions) {
            //忽略<transition/>组件
            const child = componentOptions.tag === 'transition' ? componentOptions.children[0] : vnode
            componentOptions = child && child.componentOptions

            if (componentOptions) {
                const key = getCacheKey(this.$route, componentOptions)
                const {include, cache} = this

                if (include && !include.includes(key)) {
                    return vnode
                }

                if (cache[key]) {
                    child.componentInstance = cache[key].componentInstance
                }
                else cache[key] = child

                child.data.keepAlive = true
            }
        }

        return vnode || (slot && slot[0])
    }
}
