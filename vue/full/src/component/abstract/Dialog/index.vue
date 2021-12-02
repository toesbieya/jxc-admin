<script type="text/jsx">
import cssVar from '@/style/var.scss'
import LoadingMask from './LoadingMask'

export default {
    name: "AbstractDialog",

    functional: true,

    props: {
        value: Boolean,
        loading: {type: Boolean, default: true},
        width: {type: String, default: '30%'}
    },

    render(h, context) {
        const {data, children, listeners, props: {value, loading, width}} = context

        const onClose = function () {
            return listeners.close ? () => listeners.close(false) : () => ({})
        }()

        return (
            <el-dialog
                v-drag-dialog
                visible={value}
                custom-class="abstract-dialog"
                top={cssVar.dialogTop}
                width={width}
                on-close={onClose}
                {...data}
            >
                <div class="abstract-dialog-container">
                    <LoadingMask show={loading}/>
                    <el-scrollbar>
                        <div class="abstract-dialog-body">
                            {children}
                        </div>
                        <div style="margin-bottom: 17px"/>
                    </el-scrollbar>
                </div>
            </el-dialog>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
