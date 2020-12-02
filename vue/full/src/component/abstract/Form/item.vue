<script type="text/jsx">
export default {
    name: "AbstractFormItem",

    functional: true,

    props: {full: Boolean, thin: Boolean, label: String, prop: String},

    render(h, context) {
        const {full, thin, label, prop} = context.props
        let props = {xs: 24, sm: 12, lg: 8}

        if (full) props = {span: 24}
        else if (thin) props.lg = 12

        return (
            <el-col {...{props}}>
                <el-form-item label={label} prop={prop}>
                    {context.children}
                </el-form-item>
            </el-col>
        )
    }
}
</script>
