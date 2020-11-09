<script type="text/jsx">
import RootSidebar from './root'
import SubSidebar from './sub'

export default {
    functional: true,

    render() {
        return (
            <aside class="aside">
                <RootSidebar/>
                <SubSidebar/>
            </aside>
        )
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

$background-color: #1d2935;

.root-sidebar-container {
    width: $aside-icon-size + 20px * 2;
    position: relative;
}

.root-sidebar {
    display: flex;
    flex-direction: column;
    position: absolute;
    height: 100vh;
    width: $aside-width;
    background-color: $background-color;
    overflow: hidden;
    z-index: 11;
    border-right: 1px solid #101117;
    transition: all 0.2s ease-in-out;

    > .el-menu--vertical.el-menu {
        background-color: $background-color;
    }

    //折叠状态
    &.collapse {
        width: $aside-icon-size + 20px * 2;

        .menu-item-content {
            display: none;
        }
    }
}

.sub-sidebar {
    display: flex;
    flex-direction: column;
    width: $aside-width;
    background-color: $aside-menu-background;
    font-size: 0;
    height: 100vh;
    z-index: 10;
    box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
    transition: all 0.2s ease-in-out;

    //折叠状态
    &.collapse {
        width: $aside-icon-size + 20px * 2;

        .sub-sidebar-title,
        .menu-item-content {
            display: none;
        }
    }

    &-title {
        height: $nav-height;
        line-height: $nav-height;
        padding-left: 20px;
        color: #ffffff;
        font-size: 16px;
    }
}
</style>
