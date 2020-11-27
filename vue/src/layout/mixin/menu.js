/**
 * 顶部菜单和侧边栏菜单的公共混入
 */
import {mutations as tagsViewMutations} from "@/layout/store/tagsView"
import {refreshPage} from "@/util/route"
import {isExternal} from "@/util/validate"
import {findComponentByTag} from "@/util/vue"

export default {
    data() {
        return {
            //当前激活的菜单的fullPath
            //之所以手动维护是因为el-menu在点击后就会设置activeIndex
            activeMenu: '',

            //用于判断鼠标是否在弹出菜单内
            openedMenuNum: 0
        }
    },

    methods: {
        //点击菜单后的动作
        actionOnSelectMenu(fullPath, refreshWhenSame = true) {
            //外部链接时打开新窗口
            if (isExternal(fullPath)) {
                window.open(fullPath)
                return this.resetActiveMenu()
            }

            //触发的菜单路径是当前路由时，根据参数判断是否进行刷新
            if (this.$route.path === fullPath) {
                if (!refreshWhenSame) return
                tagsViewMutations.delCacheOnly(this.$route)
                this.$nextTick(() => refreshPage())
            }
            else this.$router.push(fullPath)
        },

        //由于侧边栏菜单数组更新后，el-menu不一定会更新（当数组中不存在单级菜单时）
        //所以手动更新el-menu的当前高亮菜单
        resetActiveMenu() {
            const menu = this.$_getElMenuInstance()
            menu && menu.updateActiveIndex(this.activeMenu)
        },

        //获取el-menu实例
        //目前被侧边栏和双层侧边栏的主菜单使用
        $_getElMenuInstance() {
            if (!this.$_elMenuInstance) {
                this.$_elMenuInstance = findComponentByTag(this, 'el-menu')
            }

            return this.$_elMenuInstance
        }
    }
}
