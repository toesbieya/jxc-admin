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
                    {showTitle && <h1 class="logo-title">{title}</h1>}
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
    position: relative;
    width: 100%;
    height: $nav-height;
    line-height: $nav-height;
    padding-left: 20px;

    .logo-link {
        display: block;
        height: 100%;

        > img {
            display: inline-block;
            height: $logoSize;
            vertical-align: middle;

            & + .logo-title {
                margin-left: 12px;
            }
        }

        .logo-title {
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

    &.no-title {
        padding-left: ($aside-icon-size + 20px * 2 - $logoSize) / 2;
    }
}
</style>
