/**
 * 多页签快捷键混入
 * ctrl + →，下一个页签
 * ctrl + ←，上一个页签
 */

import {tagsViewGetters} from "el-admin-layout"
import {getRouterKey} from "el-admin-layout/src/config/logic"

export default {
    watch: {
        'setting.tagsView.shortcut': {
            immediate: true,
            handler(v) {
                //尝试移除之前可能添加的事件
                window.removeEventListener('keydown', this.shortcutsListen)

                v && window.addEventListener('keydown', this.shortcutsListen)
            }
        }
    },

    methods: {
        gotoViewByDirection(direction) {
            const views = tagsViewGetters.visitedViews
            if (views.length <= 1) return

            const key = getRouterKey(this.$route)
            let index = views.findIndex(view => view.key === key)
            if (index < 0) return

            //上一个页签
            if (direction === 'front') {
                index = index === 0 ? views.length - 1 : index - 1
            }
            //下一个页签
            else if (direction === 'behind') {
                index = index > views.length - 2 ? 0 : index + 1
            }

            return this.$router.push(views[index])
        },

        //快捷键监听
        shortcutsListen(e) {
            if (e.ctrlKey) {
                switch (e.key) {
                    //ctrl + →
                    case 'ArrowRight':
                        e.preventDefault()
                        return this.gotoViewByDirection('behind')
                    //ctrl + ←
                    case 'ArrowLeft':
                        e.preventDefault()
                        return this.gotoViewByDirection('front')
                }
            }
        }
    },

    beforeDestroy() {
        window.removeEventListener('keydown', this.shortcutsListen)
    }
}
