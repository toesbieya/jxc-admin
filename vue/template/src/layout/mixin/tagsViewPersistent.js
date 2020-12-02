/**
 * 多页签持久化混入
 * 初始化时读取本地存储中的数据
 * 页签变化时写入本地存储
 */

import {getters as tagsViewGetters, mutations as tagsViewMutations} from "@/layout/store/tagsView"
import {debounce} from "@/util"
import {getTagsView, setTagsView} from "@/util/storage"

export default {
    computed: {
        shouldPersistent: () => tagsViewGetters.persistent
    },

    watch: {
        shouldPersistent: {
            immediate: true,
            handler(v) {
                //尝试清除之前的watch
                if (this.watchVisitedViewsCallback) {
                    this.watchVisitedViewsCallback()
                    this.watchVisitedViewsCallback = null
                }

                //停用持久化时清空本地存储的页签数据
                if (!v) return setTagsView()

                //启用时先存储一次（仅在mounted后，否则此时页签数据不完整）
                this._isMounted && this.persistentTagsView(this.visitedViews)

                this.watchVisitedViewsCallback = this.$watch('visitedViews', this.persistentTagsView)
            }
        }
    },

    beforeCreate() {
        this.persistentTagsView = debounce(setTagsView)
    },

    mounted() {
        if (!this.shouldPersistent) return

        const tags = getTagsView()
        Array.isArray(tags) && tags.forEach(tagsViewMutations.addTagOnly)
    }
}
