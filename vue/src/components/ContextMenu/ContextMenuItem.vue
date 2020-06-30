<script type="text/jsx">
    export default {
        name: "ContextMenuItem",

        functional: true,

        render(h, context) {
            return <li class="contextmenu-item" {...context.data}>{context.children}</li>
        }
    }
</script>
