<script type="text/jsx">
export default {
    name: 'Hamburger',

    functional: true,

    props: {active: Boolean},

    render(h, context) {
        const {active} = context.props
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
