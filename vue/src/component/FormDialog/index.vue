<script type="text/jsx">
import LoadingMask from './LoadingMask'

export default {
    name: "FormDialog",

    functional: true,

    props: {
        value: Boolean,
        title: String,
        loading: {type: Boolean, default: true},
        top: {type: String, default: '50px'},
        width: {type: String, default: '30%'}
    },

    render(h, context) {
        const {value, title, loading, top, width} = context.props
        const l = context.listeners

        function onClose() {
            l.input && l.input(false)
        }

        return (
            <el-dialog
                v-drag-dialog
                visible={value}
                custom-class="form-dialog"
                top={top}
                width={width}
                on-close={onClose}
                {...context.data}
            >
                <slot slot="title" name="title">{title}</slot>

                <div class="form-dialog-container">
                    <LoadingMask show={loading}/>
                    <el-scrollbar>
                        <div class="form-dialog-body">
                            {context.children}
                        </div>
                        <div style="margin-bottom: 17px"/>
                    </el-scrollbar>
                </div>

                <slot slot="footer" name="footer"/>
            </el-dialog>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
