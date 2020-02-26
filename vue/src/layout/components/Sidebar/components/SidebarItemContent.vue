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

            let iconComponent

            if (icon) {
                const isElIcon = icon.startsWith('el-icon-')
                iconComponent = isElIcon ? <i class={icon + ' svg-icon'}/> : <svg-icon icon={icon}/>
            }
            else iconComponent = <span class="svg-icon"/>

            vnodes.push(iconComponent)

            if (title) vnodes.push(<span slot="title" class="sidebar-item-content">{(title)}</span>)

            return vnodes
        }
    }
</script>
