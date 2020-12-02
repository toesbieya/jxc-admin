<script type="text/jsx">
export default {
    name: "DocSteps",

    functional: true,

    props: {status: Number, finish: Number},

    render(h, context) {
        const {status, finish} = context.props
        const active = status === 2 && (finish === undefined || finish === 2) ? 4 : status
        return (
            <el-steps align-center active={active === 2 ? 3 : active}>
                <el-step title="填写单据"/>
                <el-step title="提交单据，等待审核"/>
                <el-step title="审核通过"/>
                <el-step title="单据完成"/>
            </el-steps>
        )
    }
}
</script>
