<script>
    import Tree from './Tree'
    import Tab from './Tab'

    export default {
        name: "RegionSelector",

        functional: true,

        props: {
            type: {type: String, default: 'tab', validator: v => ['tree', 'tab'].includes(v)}
        },

        render(h, context) {
            return h(context.props.type === 'tree' ? Tree : Tab, context.data)
        }
    }
</script>
