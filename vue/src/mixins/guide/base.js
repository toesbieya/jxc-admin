import {delAllUrlParam, transformGuideSteps} from "@/utils"

export default {
    methods: {
        $_prepareGuide(params) {
            if (!['navbar', 'sidebar'].includes(this.$options.name)) return
            if ('guide' in params) {
                delAllUrlParam()
                if (params.navbar && this.$options.name === 'navbar') this.$guide(params.guide, this.steps)
                else if (params.sidebar && this.$options.name === 'sidebar') this.$guide(params.guide, this.steps)
            }
        }
    },
    computed: {
        params() {
            return this.$route.params
        }
    },
    watch: {
        params(v) {
            this.$_prepareGuide(v)
        }
    },
    created() {
        transformGuideSteps(this, this.steps)
    },
    activated() {
        let params = this.$route.params
        if (!params.navbar && !params.sidebar && 'guide' in params) {
            delAllUrlParam()
            this.$guide(params.guide, this.steps)
        }
    }
}
