<script type="text/jsx">
import OnePart from './component/OnePartRoot'
import TwoPartRoot from './component/TwoPartRoot'
import TwoPartSub from './component/TwoPartSub'
import {getters as appGetters} from "@/layout/store/app"
import {getters as asideGetters} from "@/layout/store/aside"

export default {
    functional: true,

    render() {
        let children

        //移动端只能使用抽屉模式的单层侧边栏
        if (appGetters.isMobile) {
            children = <OnePart/>
        }
        else {
            switch (appGetters.navMode) {
                case 'mix':
                case 'aside':
                    children = <OnePart/>
                    break
                case 'aside-two-part':
                    children = [<TwoPartRoot/>, <TwoPartSub/>]
            }
        }

        return <aside class={`aside ${asideGetters.theme}`}>{children}</aside>
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
