<script type="text/jsx">
import Empty from "@/components/Empty"

export default {
    name: "AbstractTable",

    functional: true,

    render(h, context) {
        const {data, children, scopedSlots} = context

        if (!scopedSlots.empty) {
            scopedSlots.empty = () => <Empty/>
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
                {
                    Object
                        .entries(scopedSlots)
                        .map(([name, slot]) => (
                            name !== 'default' && <slot slot={name}>{slot()}</slot>
                        ))
                }
            </el-table>
        )
    }
}
</script>
