<script type="text/jsx">
import KeepRouterViewAlive from "./KeepRouterViewAlive"

export default {
    name: "PageView",

    functional: true,

    props: {include: Array, transitionName: String, cacheable: Boolean},

    render(h, context) {
        const {include, transitionName, cacheable} = context.props

        let view = (
            <transition name={transitionName} mode="out-in">
                <router-view/>
            </transition>
        )

        if (cacheable) {
            view = <KeepRouterViewAlive include={include}>{view}</KeepRouterViewAlive>
        }

        return <div class="page-view">{view}</div>
    }
}
</script>
