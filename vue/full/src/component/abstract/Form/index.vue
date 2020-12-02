<script type="text/jsx">
export default {
    name: "AbstractForm",

    functional: true,

    render(h, context) {
        const {data, children} = context
        return (
            <el-form
                ref="form"
                label-position="right"
                label-width="100px"
                label-suffix="："
                size="small"
                status-icon
                {...data}
            >
                <el-row>{children}</el-row>
            </el-form>
        )
    }
}
</script>
