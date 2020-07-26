<script type="text/jsx">
    export default {
        name: 'Hamburger',

        functional: true,

        props: {active: Boolean},

        render(h, context) {
            const {active} = context.props
            const className = {'navbar-icon': true, 'el-icon-s-unfold': true, 'hamburger': true, 'is-active': active}
            return (
                <div {...context.data}>
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
