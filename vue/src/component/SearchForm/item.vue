<script type="text/jsx">
export default {
    name: "SearchFormItem",

    inject: {
        searchForm: {
            default: () => ({width: 'auto'})
        }
    },

    props: {label: String},

    render() {
        const {width} = this.searchForm
        return (
            <el-col style={{width}} span={100}>
                <el-form-item label={this.label}>
                    {this.$slots.default}
                </el-form-item>
            </el-col>
        )
    }
}
</script>
