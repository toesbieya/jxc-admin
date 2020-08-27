<script type="text/jsx">
import KeepRouterViewAlive from "./KeepRouterViewAlive"

export default {
    name: "PageView",

    functional: true,

    props: {
        include: Array,
        transitionName: String
    },

    render(h, context) {
        const {include, transitionName} = context.props
        return (
            <div class="page-view">
                <KeepRouterViewAlive include={include}>
                    <transition name={transitionName} mode="out-in">
                        <router-view/>
                    </transition>
                </KeepRouterViewAlive>
            </div>
        )
    }
}
</script>
