<script type="text/jsx">
    export default {
        name: "DialogFormItem",
        functional: true,
        props: {full: Boolean, label: String, prop: String},
        render(h, context) {
            let {full, label, prop} = context.props
            let attrs = {xs: 24, sm: 12, lg: 8}
            if (full) attrs = {span: 24}
            return (
                <el-col {...{attrs}}>
                    <el-form-item label={label} prop={prop}>
                        {context.children}
                    </el-form-item>
                </el-col>
            )
        }
    }
</script>
