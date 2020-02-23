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

            if (icon) {
                const isElIcon = icon.startsWith('el-icon-')
                vnodes.push(isElIcon ? <i class={icon}/> : <svg-icon icon={icon}/>)
            }

            if (title) {
                vnodes.push(<span slot="title" class="sidebar-item-content">{(title)}</span>)
            }
            return vnodes
        }
    }
</script>
