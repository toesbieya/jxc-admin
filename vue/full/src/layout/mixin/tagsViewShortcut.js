/**
 * 多页签快捷键混入
 * ctrl + →，下一个页签
 * ctrl + ←，上一个页签
 */

import {getters as tagsViewGetters} from "@/layout/store/tagsView"

export default {
    computed: {
        shouldBindKeydownEvent() {
            return tagsViewGetters.shortcut
        }
    },

    watch: {
        shouldBindKeydownEvent: {
            immediate: true,
            handler(v) {
                //尝试移除之前可能添加的事件
                window.removeEventListener('keydown', this.shortcutsListen)

                v && window.addEventListener('keydown', this.shortcutsListen)
            }
        }
    },

    methods: {
        //上一个页签
        gotoViewFront() {
            if (this.visitedViews.length <= 1) return
            let index = this.visitedViews.findIndex(view => view.path === this.$route.path)
            if (index < 0) return
            if (index === 0) index = this.visitedViews.length
            return this.$router.push({path: this.visitedViews[index - 1].path})
        },

        //下一个页签
        gotoViewBehind() {
            if (this.visitedViews.length <= 1) return
            let index = this.visitedViews.findIndex(view => view.path === this.$route.path)
            if (index < 0) return
            if (index + 1 > this.visitedViews.length - 1) index = -1
            return this.$router.push({path: this.visitedViews[index + 1].path})
        },

        //快捷键监听
        shortcutsListen(e) {
            if (e.ctrlKey) {
                switch (e.key) {
                    //ctrl + →
                    case 'ArrowRight':
                        e.preventDefault()
                        return this.gotoViewBehind()
                    //ctrl + ←
                    case 'ArrowLeft':
                        e.preventDefault()
                        return this.gotoViewFront()
                }
            }
        }
    },

    beforeDestroy() {
        window.removeEventListener('keydown', this.shortcutsListen)
    }
}
