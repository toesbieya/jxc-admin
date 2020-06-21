<script type="text/jsx">
    export default {
        name: "ContextMenuItem",

        functional: true,

        render(h, context) {
            return h('li', context.data, context.children)
        }
    }
</script>
