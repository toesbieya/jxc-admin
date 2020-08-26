<script type="text/jsx">

export default {
    name: 'VIcon',

    functional: true,

    props: {icon: String},

    render(h, context) {
        const {data, props: {icon}} = context
        const svgPrefix = 'svg-'

        if (icon.startsWith(svgPrefix)) {
            return (
                <svg class="icon" aria-hidden="true" {...data}>
                    <use href={`#icon-${icon.replace(svgPrefix, '')}`}/>
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
    fill: currentColor;
}
</style>
