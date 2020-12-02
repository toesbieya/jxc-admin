<script type="text/jsx">
/**
 * 从ant-design-vue处拷贝
 */
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
        else imageNode = <DefaultEmptyImage/>

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

<style lang="scss" src="./style.scss"></style>
