<script type="text/jsx">
import FormAnchor from "@/component/AbstractForm/FormAnchor"
import FormValidateInfo from "@/component/AbstractForm/FormValidateInfo"

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
    name: "DetailForm",

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

<style lang="scss">
@import "~@/asset/style/variables.scss";

.detail-page {
    &.el-loading-parent--relative {
        position: static !important;
    }

    &-header {
        margin: -#{$page-view-margin} -#{$page-view-margin} 0 -#{$page-view-margin};
        padding: 16px 32px 0 32px;
        background: #fff;
        border-bottom: 1px solid #e8eaec;

        > .el-page-header {
            padding: 0 0 24px 0;

            .el-page-header__left:hover {
                color: $--color-primary;
            }
        }

        > .el-row {
            display: flex;
            width: 100%;
            margin-bottom: 12px;

            @media (max-width: 768px) {
                display: block;
            }
        }

        &-left {
            width: 100%;
            overflow: hidden;

            .el-col {
                margin-bottom: 16px;
            }
        }

        &-extra {
            min-width: 242px;
            margin-left: 88px;

            @media (max-width: 1200px) {
                margin-left: 44px;
            }

            @media (max-width: 992px) {
                margin-left: 20px;
            }

            @media (max-width: 768px) {
                margin-left: 0;
            }

            > div {
                display: flex;
                justify-content: space-between;
                width: 200px;
            }

            &-title {
                margin-bottom: 4px;
                color: rgba(0, 0, 0, .45);
                font-size: 14px;
            }

            &-content {
                margin-top: 12px;
                color: rgba(0, 0, 0, .85);
                font-size: 24px;
            }
        }

        &-description {
            span {
                display: inline-flex;
                align-items: baseline;
            }

            &-label {
                color: rgba(0, 0, 0, .85);
                font-weight: 400;
                font-size: 14px;
                line-height: 1.5715;
                text-align: start;
            }

            &-content {
                color: rgba(0, 0, 0, .65);
                font-size: 14px;
                line-height: 1.5715;
            }
        }
    }

    > .el-form > .el-row > .el-card {
        margin-top: 16px;
    }

    &-footer {
        @include clearfix;
        position: absolute;
        width: 100%;
        right: 0;
        bottom: 0;
        z-index: 99;
        transition: all 0.3s ease 0s;
        height: 56px;
        padding: 0 24px;
        line-height: 56px;
        border-top: 1px solid #f0f0f0;
        box-shadow: 0 3px 6px -4px rgba(0, 0, 0, .12), 0 6px 16px 0 rgba(0, 0, 0, .08), 0 9px 28px 8px rgba(0, 0, 0, .05);
        backdrop-filter: blur(3px);

        &-left {
            float: left;
        }

        &-right {
            float: right;
        }
    }
}
</style>
