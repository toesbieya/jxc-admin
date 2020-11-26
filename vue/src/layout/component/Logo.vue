<script type="text/jsx">
import {title} from '@/config'

export default {
    name: 'Logo',

    functional: true,

    props: {showTitle: Boolean},

    render(h, context) {
        const {showTitle} = context.props

        const logoClass = {'logo-container': true, 'no-title': !showTitle}

        return (
            <div class={logoClass}>
                <router-link class="logo-link" to="/">
                    <img src={`${process.env.BASE_URL}static/img/logo.svg`}/>
                    {showTitle && <h1>{title}</h1>}
                </router-link>
            </div>
        )
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

$logoSize: 30px;

.logo-container {
    width: 100%;
    height: $nav-height;
    line-height: $nav-height;
    padding-left: $menu-padding;

    .logo-link {
        display: block;
        height: 100%;

        > img {
            display: inline-block;
            height: $logoSize;
            vertical-align: middle;

            & + h1 {
                margin-left: 12px;
            }
        }

        > h1 {
            display: inline-block;
            margin: 0;
            color: $--color-primary;
            font-weight: 600;
            line-height: $nav-height;
            font-size: 18px;
            vertical-align: middle;
        }
    }

    &.no-title {
        padding-left: ($aside-collapse-width - $logoSize) / 2;
    }
}

.dark .logo-container .logo-link > h1 {
    color: #ffffff;
}
</style>
