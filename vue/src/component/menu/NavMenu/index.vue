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
        showIconMaxDepth: {type: Number, default: 1}
    },

    render(h, context) {
        const {data, props: {mode, menus, collapse, showParentOnCollapse, showIconMaxDepth}} = context

        return (
            <el-menu
                ref="menu"
                class={`el-menu--${mode}`}
                mode={mode}
                collapse={collapse}
                collapse-transition={false}
                {...data}
            >
                {menus.map(m => (
                    <Item
                        menu={m}
                        show-parent={showParentOnCollapse}
                        collapse={collapse}
                        show-icon-max-depth={showIconMaxDepth}
                    />
                ))}
            </el-menu>
        )
    }
}
</script>
