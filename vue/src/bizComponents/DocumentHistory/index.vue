<script type="text/jsx">
    export default {
        name: "DocumentHistory",
        functional: true,
        props: {
            type: String,
            data: {type: Array, default: () => []}
        },
        render(h, context) {
            const {type, data = []} = context.props
            if (type === 'add') return ''
            return (
                <el-popover
                    v-on:show={context.listeners.show}
                    placement="bottom"
                    width="250"
                    trigger="click"
                >
                    <el-timeline>
                        <el-scrollbar wrap-class="el-select-dropdown__wrap">
                            {
                                data.map(activity => (
                                    <el-timeline-item
                                        key={activity.id}
                                        timestamp={activity.time}
                                        placement="top"
                                    >
                                        <p>{activity.uname} {activity.type}</p>
                                        <p>{activity.info}</p>
                                    </el-timeline-item>
                                ))
                            }
                        </el-scrollbar>
                        {data.length <= 0 ? <div className="el-timeline__empty">暂无记录</div> : ''}
                    </el-timeline>
                    <button slot="reference" class="el-dialog__headerbtn" style="right: 60px" title="历史状态记录">
                        <i class="el-icon-document el-dialog__close"/>
                    </button>
                </el-popover>
            )
        }
    }
</script>
