<script type="text/jsx">
import jumpOnSelectMenuMixin from "@/layout/mixin/jumpOnSelectMenu"

export default {
    name: "RootMenu",

    mixins: [jumpOnSelectMenuMixin],

    props: {
        //当前激活菜单的路径
        activeMenu: String,
        //树形菜单数组
        menu: Array,
        //是否在顶部菜单个数<=1时仍然渲染
        alwaysShow: Boolean
    },

    methods: {
        onSelect(menu) {
            const path = menu.fullPath
            if (path === this.activeMenu) return
            this.jumpOnSelectMenu(path, false)
        }
    },

    render() {
        //菜单点击时触发select事件，payload为菜单对象
        const {activeMenu, menu, alwaysShow} = this
        if (!Array.isArray(menu) || menu.length <= 1 && !alwaysShow) return
        return (
            <ul class="root-menu">
                {menu.map(i => {
                    const key = i.fullPath + i.meta.title
                    const className = {'root-menu-item': true, 'active': i.fullPath === activeMenu}
                    const icon = i.meta.icon
                    return (
                        <li key={key} class={className} on-click={() => this.onSelect(i)}>
                            {icon && <v-icon icon={icon}/>}
                            {i.meta.title}
                        </li>
                    )
                })}
            </ul>
        )
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.root-menu {
    height: $nav-height;
    line-height: $nav-height;
    list-style: none;
    margin: 0;
    padding: 0;
    font-size: 14px;
    color: #515a6e;

    &-item {
        height: inherit;
        border-bottom: 2px solid transparent;
        float: left;
        padding: 0 20px;
        cursor: pointer;
        transition: all .2s ease-in-out;

        > .icon {
            margin-right: 6px;
            vertical-align: -0.125em;
        }

        &:hover, &.active {
            color: $--color-primary;
            border-bottom: 2px solid $--color-primary;
        }
    }
}
</style>
