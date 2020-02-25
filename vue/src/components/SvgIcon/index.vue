<script type="text/jsx">
    import {isExternal} from '@/utils/validate'

    export default {
        name: 'SvgIcon',
        functional: true,
        props: {
            icon: {
                type: String,
                required: true
            },
            className: {
                type: String,
                default: ''
            }
        },
        render(h, context) {
            const {listeners} = context
            const {icon, className} = context.props
            const c = 'svg-icon ' + (className || '')
            if (isExternal(icon)) {
                const style = {
                    mask: `url(${this.icon}) no-repeat 50% 50%`,
                    '-webkit-mask': `url(${this.icon}) no-repeat 50% 50%`
                }
                return <div {...{on: listeners}} class={'svg-external-icon ' + c} style={style}/>
            }
            else {
                const iconName = `#icon-${icon}`
                return (
                    <svg {...{on: listeners}} class={c} aria-hidden="true">
                        <use href={iconName}/>
                    </svg>
                )
            }
        }
    }
</script>

<style>
    .svg-icon {
        width: 1em;
        height: 1em;
        fill: currentColor;
        overflow: hidden;
    }

    .svg-external-icon {
        background-color: currentColor;
        mask-size: cover !important;
        display: inline-block;
    }
</style>
