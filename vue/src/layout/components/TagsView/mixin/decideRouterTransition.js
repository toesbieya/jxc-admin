import {leftSideRouteTransition, rightSideRouteTransition} from '@/config'

export default {
    methods: {
        //根据访问的tab页的左右顺序来确定路由动画
        decideRouteTransition(to, from) {
            let fromIndex = this.visitedViews.findIndex(i => i.path === from.path)
            if (fromIndex < 0) return this.$store.commit('tagsView/transitionName', rightSideRouteTransition)

            let toIndex = this.visitedViews.findIndex(i => i.path === to.path)
            if (toIndex < 0) return this.$store.commit('tagsView/transitionName', rightSideRouteTransition)

            this.$store.commit('tagsView/transitionName', toIndex > fromIndex ? rightSideRouteTransition : leftSideRouteTransition)
        },
    }
}
