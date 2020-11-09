<script type="text/jsx">
import Item from './item'

export default {
    name: "NavMenu",

    functional: true,

    props: {
        menus: Array,
        mode: {type: String, default: 'vertical'},
        collapse: Boolean,
        showParentOnCollapse: Boolean,
        showIconMaxDepth: {type: Number, default: 1},
        switchTransition: Boolean,
        switchTransitionName: {type: String, default: 'sidebar'}
    },

    render(h, context) {
        const {
            data, children,
            props: {
                mode, menus, collapse, showParentOnCollapse,
                showIconMaxDepth, switchTransition, switchTransitionName
            }
        } = context

        let items = children || menus.map(m => (
            <Item
                menu={m}
                show-parent={showParentOnCollapse}
                collapse={collapse}
                show-icon-max-depth={showIconMaxDepth}
            />
        ))
        if (switchTransition) {
            items = <transition-group name={switchTransitionName}>{items}</transition-group>
        }

        return (
            <el-menu
                ref="menu"
                class={`el-menu--${mode}`}
                mode={mode}
                collapse={collapse}
                collapse-transition={false}
                {...data}
            >
                {items}
            </el-menu>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
