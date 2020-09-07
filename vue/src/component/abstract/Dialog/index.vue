<script type="text/jsx">
import LoadingMask from './LoadingMask'

export default {
    name: "AbstractDialog",

    functional: true,

    props: {
        value: Boolean,
        title: String,
        loading: {type: Boolean, default: true},
        top: {type: String, default: '50px'},
        width: {type: String, default: '30%'}
    },

    render(h, context) {
        const {data, children, listeners, props: {value, title, loading, top, width}} = context

        function onClose() {
            listeners.input && listeners.input(false)
        }

        return (
            <el-dialog
                v-drag-dialog
                visible={value}
                custom-class="abstract-dialog"
                top={top}
                width={width}
                on-close={onClose}
                {...data}
            >
                <slot slot="title" name="title">{title}</slot>

                <div class="abstract-dialog-container">
                    <LoadingMask show={loading}/>
                    <el-scrollbar>
                        <div class="abstract-dialog-body">
                            {children}
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
