<script type="text/jsx">
import Empty from "@/component/Empty"

export default {
    name: "AbstractTable",

    functional: true,

    render(h, context) {
        const {data, children} = context

        if (!data.scopedSlots) {
            data.scopedSlots = {}
        }
        if (!data.scopedSlots.empty) {
            data.scopedSlots.empty = () => <Empty/>
        }

        return (
            <el-table
                ref="table"
                current-row-key="id"
                row-key="id"
                highlight-current-row
                {...data}
            >
                {children}
            </el-table>
        )
    }
}
</script>
