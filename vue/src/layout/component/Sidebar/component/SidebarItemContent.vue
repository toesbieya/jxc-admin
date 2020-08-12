<script type="text/jsx">
export default {
    name: "SidebarItemContent",

    functional: true,

    props: {
        icon: String,
        title: String
    },

    render(h, context) {
        const {icon, title} = context.props
        const vnodes = []

        const iconComponent = icon ? <v-icon icon={icon}/> : <span class="icon"/>

        vnodes.push(iconComponent)

        if (title) vnodes.push(<span slot="title" class="sidebar-item-content">{title}</span>)

        return vnodes
    }
}
</script>
