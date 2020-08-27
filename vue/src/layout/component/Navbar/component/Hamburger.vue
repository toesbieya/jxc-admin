<script type="text/jsx">
export default {
    name: 'Hamburger',

    functional: true,

    props: {render: Boolean, active: Boolean},

    render(h, context) {
        const {render, active} = context.props
        if (!render) return
        const className = `navbar-icon el-icon-s-unfold hamburger ${active ? 'is-active' : ''}`
        return (
            <div {...context.data} title={`${active ? '折叠' : '展开'}侧边栏`}>
                <i class={className}/>
            </div>
        )
    }
}
</script>

<style>
.hamburger {
    font-size: 20px;
}

.hamburger.is-active {
    transform: rotate(180deg);
}
</style>
