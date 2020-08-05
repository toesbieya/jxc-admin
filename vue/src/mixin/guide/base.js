import {delAllUrlParam} from "@/util"

//将引导步骤中函数的this绑定为组件实例
function transformGuideSteps(instance, steps) {
    steps.forEach(step => {
        Object.keys(step).forEach(key => {
            if (typeof step[key] === 'function') {
                step[key] = step[key].bind(instance)
            }
        })
    })
}

export default {
    methods: {
        $_prepareGuide(params) {
            if (!['navbar', 'sidebar'].includes(this.$options.name)) return

            if ('guide' in params) {
                delAllUrlParam()
                for (const key of ['navbar', 'sidebar']) {
                    if (params[key] && this.$options.name === key) {
                        return this.$guide(params.guide, this.guideSteps)
                    }
                }
            }
        }
    },

    computed: {
        routeParams() {
            return this.$route.params
        }
    },

    watch: {
        routeParams(v) {
            this.$_prepareGuide(v)
        }
    },

    created() {
        transformGuideSteps(this, this.guideSteps)
    },

    activated() {
        const params = this.$route.params
        if (!params.navbar && !params.sidebar && 'guide' in params) {
            delAllUrlParam()
            this.$guide(params.guide, this.guideSteps)
        }
    }
}
