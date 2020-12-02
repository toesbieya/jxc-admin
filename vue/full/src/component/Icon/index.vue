<script type="text/jsx">
const SVG_PREFIX = 'svg-'

export default {
    name: 'VIcon',

    functional: true,

    props: {icon: String},

    render(h, context) {
        const {data, props: {icon}} = context

        if (!icon) return

        //渲染成svg
        if (icon.startsWith(SVG_PREFIX)) {
            return (
                <svg class="icon svg" aria-hidden="true" {...data}>
                    <use href={`#icon-${icon.replace(SVG_PREFIX, '')}`}/>
                </svg>
            )
        }

        return <i class={`icon ${icon}`} {...data}/>
    }
}
</script>

<style>
.icon {
    width: 1em;
    height: 1em;
}

.icon.svg {
    fill: currentColor;
}
</style>
