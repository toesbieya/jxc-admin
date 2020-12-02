<script type="text/jsx">
import {getters as appGetters} from "@/layout/store/app"
import {getters as asideGetters, mutations as asideMutations} from "@/layout/store/aside"

export default {
    name: 'Hamburger',

    render() {
        //考虑侧边栏可能是抽屉的情况
        const collapsed = appGetters.isMobile ? asideGetters.show : !asideGetters.collapse
        const className = `hamburger ${collapsed ? 'collapsed' : ''}`

        return (
            <div
                class={className}
                title={`${collapsed ? '折叠' : '展开'}侧边栏`}
                on-click={asideMutations.switch}
            >
                <i class="el-icon-s-unfold"/>
            </div>
        )
    }
}
</script>

<style>
.hamburger.collapsed > i {
    transform: rotate(180deg);
}
</style>
