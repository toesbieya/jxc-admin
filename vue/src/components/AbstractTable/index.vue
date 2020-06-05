<script type="text/jsx">
    import Empty from '@/components/Empty'

    export default {
        name: "AbstractTable",
        functional: true,
        render(h, context) {
            const {data, children} = context
            return (
                <el-table
                    ref="table"
                    current-row-key="id"
                    row-key="id"
                    highlight-current-row
                    {...data}
                >
                    {children}
                    <Empty slot="empty"/>
                </el-table>
            )
        }
    }
</script>
