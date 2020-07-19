<script type="text/jsx">
    export default {
        name: "AbstractFormItem",

        functional: true,

        props: {full: Boolean, dense: Boolean, label: String, prop: String},

        render(h, context) {
            const {full, dense, label, prop} = context.props
            let attrs = {xs: 24, sm: 12, lg: 8}

            if (full) attrs = {span: 24}
            else if (dense) attrs.lg = 12

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
