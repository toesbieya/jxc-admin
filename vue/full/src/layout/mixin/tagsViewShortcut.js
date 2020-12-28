/**
 * 多页签快捷键混入
 * ctrl + →，下一个页签
 * ctrl + ←，上一个页签
 */

import {tagsViewGetters} from "el-admin-layout"

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
            const views = tagsViewGetters.visitedViews

            if (views.length <= 1) return

            let index = views.findIndex(view => view.path === this.$route.path)

            if (index < 0) return

            if (index === 0) {
                index = views.length
            }

            return this.$router.push({path: views[index - 1].path})
        },

        //下一个页签
        gotoViewBehind() {
            const views = tagsViewGetters.visitedViews

            if (views.length <= 1) return

            let index = views.findIndex(view => view.path === this.$route.path)

            if (index < 0) return

            if (index + 1 > views.length - 1) {
                index = -1
            }

            return this.$router.push({path: views[index + 1].path})
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
