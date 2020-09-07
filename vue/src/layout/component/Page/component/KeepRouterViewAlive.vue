<script>
import {isEmpty} from "@/util"
import {getRouterViewCacheKey} from "@/layout/util"

const KEY = '_routerViewKey'

function getFirstComponentChild(children) {
    if (Array.isArray(children)) {
        return children.find(c => !isEmpty(c) && (!isEmpty(c.componentOptions) || c.isComment && c.asyncFactory))
    }
}

function removeCache(cache, key) {
    const cached = cache[key]
    cached && cached.componentInstance && cached.componentInstance.$destroy()
    delete cache[key]
}

function getCacheKey(route, componentOptions) {
    if (KEY in componentOptions) return componentOptions[KEY]

    const key = getRouterViewCacheKey(route)

    if (key) componentOptions[KEY] = key

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
                const {cache, $route} = this
                for (const [k, v] of Object.entries(cache)) {
                    const cacheKey = getCacheKey($route, v.componentOptions)
                    if (!val || !val.includes(cacheKey)) {
                        removeCache(cache, k)
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
            removeCache(this.cache, key)
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
</script>
