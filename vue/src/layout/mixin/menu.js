/**
 * 顶部菜单和侧边栏菜单的公共混入
 */
import {mutations as tagsViewMutations} from "@/layout/store/tagsView"
import {isExternal} from "@/util/validate"

export default {
    data() {
        return {
            //当前激活的菜单的fullPath
            activeMenu: ''
        }
    },

    methods: {
        //点击菜单后的动作
        actionOnSelectMenu(fullPath, refreshWhenSame = true) {
            //外部链接时打开新窗口
            if (isExternal(fullPath)) {
                window.open(fullPath)
                //重置高亮菜单
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
        },

        resetActiveMenu() {
            //由于侧边栏菜单数组更新后，el-menu不一定会更新（当数组中不存在单级菜单时）
            //所以手动更新el-menu的当前高亮菜单
            const menu = this.$refs.menu
            menu && menu.updateActiveIndex()
        }
    }
}
