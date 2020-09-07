<script type="jsx">
export default {
    name: "AbstractPagination",

    functional: true,

    props: {
        model: {
            type: Object,
            default: () => ({total: 0, page: 1, pageSize: 15})
        }
    },

    render(h, context) {
        const {model: {total, page, pageSize}} = context.props
        return (
            <el-pagination
                background
                current-page={page}
                page-size={pageSize}
                total={total}
                hide-on-single-page
                layout="total, prev, pager, next, jumper"
                {...context.data}
            />
        )
    }
}
</script>
