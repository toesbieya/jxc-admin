<script type="text/jsx">
    import DefaultEmptyImage from "./DefaultEmptyImage"

    export default {
        name: "AEmpty",
        functional: true,
        props: {
            description: String,
            image: [String, Object],
            imageStyle: [String, Object]
        },
        render(h, context) {
            const {description = '暂无数据', image, imageStyle} = context.props
            const prefixCls = 'ant-empty'
            const cls = {[prefixCls]: true}

            let imageNode = null
            if (typeof image === 'string') {
                imageNode = <img alt={description} src={image}/>
            }
            else {
                imageNode = <DefaultEmptyImage/>
            }
            return (
                <div class={cls} {...{on: context.listeners}}>
                    <div class={`${prefixCls}-image`} style={imageStyle}>{imageNode}</div>
                    {description && <p class={`${prefixCls}-description`}>{description}</p>}
                    {context.children && <div class={`${prefixCls}-footer`}>{context.children}</div>}
                </div>
            )
        }
    }
</script>

<style lang="scss">
    .ant-empty {
        margin: 0 8px;
        font-size: 14px;
        line-height: 22px;
        text-align: center;

        &-image {
            height: 100px;
            margin-bottom: 8px;

            img {
                height: 100%;
            }

            svg {
                height: 100%;
                margin: auto;
            }
        }

        &-description {
            margin: 0;
        }

        &-footer {
            margin-top: 16px;
        }

        &-normal {
            margin: 32px 0;
            color: rgba(0, 0, 0, .25);

            .ant-empty-image {
                height: 40px;
            }
        }

        &-small {
            margin: 8px 0;
            color: rgba(0, 0, 0, .25);

            .ant-empty-image {
                height: 35px;
            }
        }
    }
</style>
