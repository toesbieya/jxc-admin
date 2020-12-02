<script type="jsx">
import FormAnchor from "@/component/form/Anchor"
import FormValidateInfo from "@/component/form/ValidateInfo"

const renderHeader = (h, title, description, extra, close) => (
    <div class="detail-page-header">
        <el-page-header on-back={close} content={title}/>

        <el-row>
            <el-row class="detail-page-header-left">
                {description.map(({label, content}) => (
                    <el-col key={label} xs={24} sm={12}>
                        <span class="detail-page-header-description-label">{label}</span>
                        <span class="detail-page-header-description-content">{content}</span>
                    </el-col>
                ))}
            </el-row>

            <div class="detail-page-header-extra">
                <div>
                    {extra.map(({title, content}) => (
                        <div key={title}>
                            <div class="detail-page-header-extra-title">{title}</div>
                            <div class="detail-page-header-extra-content">{content}</div>
                        </div>
                    ))}
                </div>
            </div>
        </el-row>
    </div>
)

const renderFooter = (h, form, buttons, close) => (
    <div class="detail-page-footer">
        <div class="detail-page-footer-left">
            <slot name="left"/>
        </div>
        <div class="detail-page-footer-right">
            <FormValidateInfo form={form}/>
            {renderButtons(h, buttons, close)}
        </div>
    </div>
)

const renderButtons = (h, buttons, close) => {
    const list = buttons.map(({type, content, e}) =>
        <el-button size="small" type={type} on-click={e}>{content}</el-button>
    )
    list.unshift(<el-button plain size="small" on-click={close}>关 闭</el-button>)
    return list
}

export default {
    name: "DetailPage",

    functional: true,

    props: {
        loading: Boolean,
        reference: Function,
        anchors: {type: Array, default: () => []},
        title: String,
        description: {type: Array, default: () => []},
        extra: {type: Array, default: () => []},
        buttons: {type: Array, default: () => []},
    },

    render(h, context) {
        const {
            props: {loading, reference, anchors, title, description, extra, buttons},
            listeners: {close},
            children
        } = context

        const getFormRef = () => {
            if (!reference) return null
            const ref = reference()
            if (!ref) return null
            return ref.form
        }

        return (
            <div v-loading={loading} class="detail-page">
                {renderHeader(h, title, description, extra, close)}
                <FormAnchor reference={reference} data={anchors}/>
                {children}
                {renderFooter(h, getFormRef, buttons, close)}
            </div>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
