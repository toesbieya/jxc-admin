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
