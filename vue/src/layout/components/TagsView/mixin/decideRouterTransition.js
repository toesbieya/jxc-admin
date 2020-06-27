import {leftSideRouteTransition as left, rightSideRouteTransition as right} from '@/config'

export default {
    methods: {
        //根据访问的tab页的左右顺序来确定路由动画
        decideRouteTransition(to, from) {
            let transitionName = right

            const fromIndex = this.visitedViews.findIndex(i => i.path === from.path)
            const toIndex = this.visitedViews.findIndex(i => i.path === to.path)
            if (fromIndex < toIndex) transitionName = left

            this.$store.commit('tagsView/transitionName', transitionName)
        },
    }
}
