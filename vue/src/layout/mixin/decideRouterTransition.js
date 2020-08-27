import {route as routeConfig} from '@/config'

const {animate} = routeConfig

export default {
    methods: {
        //根据访问的tab页的左右顺序来确定路由动画
        decideRouteTransition(to, from) {
            let transitionName = animate.prev

            const fromIndex = this.visitedViews.findIndex(i => i.path === from.path)
            const toIndex = this.visitedViews.findIndex(i => i.path === to.path)

            //新开tab也认为顺序高于上一个tab
            if (toIndex === -1 || fromIndex < toIndex) transitionName = animate.next

            this.$store.commit('tagsView/transitionName', transitionName)
        },
    }
}
