<script>
const Tree = () => import('./Tree').then(_ => _.default)
const Tab = () => import('./Tab').then(_ => _.default)

export default {
    name: "RegionSelector",

    functional: true,

    props: {
        type: {
            type: String,
            default: 'tab',
            validator: v => ['tree', 'tab'].includes(v)
        },

        //省市地区json数据请求地址
        regionDataUrl: {
            type: String,
            default: `${process.env.BASE_URL}static/json/region-pca.json`
        }
    },

    render(h, context) {
        if (!context.data.props) {
            context.data.props = {}
        }
        context.data.props.regionDataUrl = context.props.regionDataUrl

        return h(context.props.type === 'tree' ? Tree : Tab, context.data)
    }
}
</script>
