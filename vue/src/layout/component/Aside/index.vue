<script type="text/jsx">
import OnePart from './OnePart'
import TwoPart from './TwoPart'
import {getters as appGetters} from "@/layout/store/app"

export default {
    functional: true,

    render() {
        //移动端只能使用抽屉模式的单层侧边栏
        if (appGetters.isMobile) {
            return <OnePart/>
        }

        switch (appGetters.navMode) {
            case 'mix':
            case 'aside':
                return <OnePart/>
            case 'aside-two-part':
                return <TwoPart/>
        }
    }
}
</script>

<style lang="scss">
.aside {
    display: flex;
    position: relative;
}

/* 侧边栏切换动画开始 */
.sidebar-enter-active {
    transition: all 0.2s;
}

.sidebar-enter,
.sidebar-leave-active {
    opacity: 0;
    transform: translateY(30px) skewY(10deg)
}

.sidebar-leave-active {
    position: absolute;
}

/* 侧边栏切换动画结束 */
</style>
