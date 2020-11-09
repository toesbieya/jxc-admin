<script type="text/jsx">
import {sidebarLogoUrl, title} from '@/config'

export default {
    name: 'SidebarLogo',

    functional: true,

    props: {collapse: Boolean},

    render(h, context) {
        const {collapse} = context.props

        const logoClass = {'sidebar-logo-container': true, 'collapse': collapse}

        return (
            <div class={logoClass}>
                <router-link class="sidebar-logo-link" to="/">
                    {sidebarLogoUrl && <img src={sidebarLogoUrl} class="sidebar-logo"/>}
                    {!collapse && <h1 class="sidebar-title">{title}</h1>}
                </router-link>
            </div>
        )
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

$logoSize: 30px;

.sidebar-logo-container {
    position: relative;
    width: 100%;
    height: $nav-height;
    line-height: $nav-height;
    padding-left: 20px;

    .sidebar-logo-link {
        height: 100%;
        width: 100%;

        .sidebar-logo {
            width: $logoSize;
            height: $logoSize;
            vertical-align: middle;

            & + .sidebar-title {
                margin-left: 12px;
            }
        }

        .sidebar-title {
            display: inline-block;
            margin: 0;
            color: #ffffff;
            font-weight: 600;
            line-height: $nav-height;
            font-size: 14px;
            font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
            vertical-align: middle;
        }
    }

    &.collapse {
        text-align: center;
        padding-left: 0;
    }
}
</style>
