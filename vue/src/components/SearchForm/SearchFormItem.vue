<script type="text/jsx">
    export default {
        name: "SearchFormItem",
        inject: {
            searchForm: {
                default: () => ({sm: 12, lg: 8, xl: 6})
            }
        },
        props: {label: String},
        render() {
            const {sm, lg, xl} = this.searchForm
            return (
                <el-col {...{attrs: {sm, lg, xl}}}>
                    <el-form-item label={this.label}>
                        {this.$slots.default}
                    </el-form-item>
                </el-col>
            )
        }
    }
</script>
