<script>
const Tree = () => import('./Tree').then(_ => _.default)
const Tab = () => import('./Tab').then(_ => _.default)

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
