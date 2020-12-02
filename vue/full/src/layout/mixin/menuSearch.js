/**
 * 侧边栏搜索框混入
 */
import MenuSearch from '@/layout/component/Aside/component/MenuSearch'
import {trim} from "@/util"
import {findComponentByTag} from "@/util/vue"

export default {
    components: {MenuSearch},

    methods: {
        $_getNavMenuInstance() {
            if (!this.$_navMenuInstance) {
                this.$_navMenuInstance = findComponentByTag(this, 'nav-menu')
            }

            return this.$_navMenuInstance
        },

        handlerSearch(v) {
            this.$_getNavMenuInstance().realSearchWord = trim(v)
        }
    }
}
