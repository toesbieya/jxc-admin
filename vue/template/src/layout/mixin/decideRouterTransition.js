import {getters as pageGetters, mutations as pageViewMutations} from "@/layout/store/page"

export default {
    watch: {
        $route(to, from) {
            this.decideRouteTransition(to, from)
        }
    },

    methods: {
        //根据访问的tab页的左右顺序来确定路由动画
        decideRouteTransition(to, from) {
            const {next, prev} = pageGetters.transition

            let transitionName = prev

            //这里认为页签数量不会太多，所以为了可读性使用两次循环查找
            const fromIndex = this.visitedViews.findIndex(i => i.path === from.path)
            const toIndex = this.visitedViews.findIndex(i => i.path === to.path)

            //新开tab也认为顺序高于上一个tab
            if (toIndex === -1 || fromIndex < toIndex) {
                transitionName = next
            }

            pageViewMutations.transition({curr: transitionName})
        },
    }
}
