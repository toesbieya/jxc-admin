import Hamburger from '@/layout/component/Hamburger'
import {getters as appGetters} from "@/layout/store/app"
import {getters as asideGetters} from "@/layout/store/aside"
import {getSidebarMenus} from "@/layout/util"

/**
 * 汉堡包的渲染条件混入
 */

export default {
    components: {Hamburger},

    computed: {
        //渲染汉堡包的条件
        //①侧边栏有菜单
        //②移动端 或
        //③桌面端且汉堡包位置正确（侧边栏引入则需要是aside，反之需要是head）
        //④桌面端且是双层侧边栏导航
        //⑤桌面端且是侧边栏导航或混合导航时，未设置侧边栏自动隐藏
        renderHamburger() {
            if (getSidebarMenus().length <= 0) return false

            const isMobile = appGetters.isMobile,
                correctPosition =
                    asideGetters.hamburgerPosition === (this.$options.name === 'navbar' ? 'head' : 'aside'),
                correctMode =
                    ['aside', 'mix'].includes(appGetters.navMode) && !asideGetters.autoHide
                    || appGetters.navMode === 'aside-two-part'

            return isMobile || correctPosition && correctMode
        }
    }
}
