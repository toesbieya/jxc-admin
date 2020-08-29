import {mutations as tagsViewMutations} from "@/layout/store/tagsView"
import {isExternal} from "@/util/validate"

export default {
    methods: {
        actionOnSelectMenu(fullPath, refreshWhenSame = true) {
            //外部链接时打开新窗口
            if (isExternal(fullPath)) {
                window.open(fullPath)
                //当是从侧边栏打开时，重置高亮菜单
                const menu = this.$refs.menu
                if (menu && menu.updateActiveIndex) {
                    menu.updateActiveIndex(this.activeMenu)
                }
                return
            }

            //触发的菜单路径是当前路由时，根据参数判断是否进行刷新
            if (this.$route.path === fullPath) {
                if (!refreshWhenSame) return
                tagsViewMutations.delCachedView(this.$route)
                this.$nextTick(() => this.$router.replace({path: '/redirect' + this.$route.fullPath}))
            }
            else this.$router.push(fullPath)
        }
    }
}
