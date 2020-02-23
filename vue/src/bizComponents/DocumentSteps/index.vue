<script type="text/jsx">
    export default {
        name: "DocumentSteps",
        functional: true,
        props: {
            status: Number,
            type: String
        },
        render(h, context) {
            let {status, type} = context.props
            let firstStatus = type === 'add' ? 'finish' : 'success'
            let secondStatus = status === 1 ? 'success' : type === 'add' ? 'wait' : 'finish'
            if (status !== 2) {
                return (
                    <el-steps align-center style="margin-top: 20px">
                        <el-step title="填写单据" status={firstStatus}/>
                        <el-step title="提交单据，等待审核" status={secondStatus}/>
                        <el-step title="审核通过" status="wait"/>
                    </el-steps>
                )
            }
        }
    }
</script>
