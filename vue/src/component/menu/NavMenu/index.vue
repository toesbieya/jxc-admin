<script type="text/jsx">
import Item from './item'

export default {
    functional: true,

    props: {
        //路由配置项组成的树形数组
        menus: Array,

        //垂直还是水平，在el-menu原效果上加了样式名
        mode: {type: String, default: 'vertical'},

        //主题，light 或 dark
        theme: {type: String, default: 'light'},

        //是否折叠
        collapse: Boolean,

        //折叠时的展开菜单是否显示父级
        showParentOnCollapse: Boolean,

        //能够显示图标的最大深度，不传 或 <0 则不作限制
        showIconMaxDepth: {type: Number, default: 1},

        //menus变化时是否使用过渡动画
        switchTransition: Boolean,

        //menus过渡动画名称
        switchTransitionName: String
    },

    render(h, context) {
        const {
            data, children,
            props: {
                mode, menus, theme, collapse, showParentOnCollapse,
                showIconMaxDepth, switchTransition, switchTransitionName
            }
        } = context

        const themeClassName = `el-menu--${theme}`

        let items = children || menus.map(m => (
            <Item
                menu={m}
                popper-class={themeClassName}
                show-parent={showParentOnCollapse}
                collapse={collapse}
                show-icon-max-depth={showIconMaxDepth}
            />
        ))

        if (switchTransition && switchTransitionName) {
            items = <transition-group name={switchTransitionName}>{items}</transition-group>
        }

        return (
            <el-menu
                ref="menu"
                class={`el-menu--${mode} ${themeClassName}`}
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
