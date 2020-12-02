<script type="text/jsx">
//由quasar处搬运，https://quasar.dev/vue-components/skeleton
import {skeletonAnimations, skeletonTypes} from './constant'

export default {
    name: 'QSkeleton',

    functional: true,

    props: {
        type: {
            type: String,
            validator: v => skeletonTypes.includes(v),
            default: 'text'
        },
        animation: {
            type: String,
            validator: v => skeletonAnimations.includes(v),
            default: 'wave'
        },
        className: String,
        tag: {type: String, default: 'div'},
        dark: Boolean,
        square: Boolean,
        bordered: Boolean,
        size: String,
        width: String,
        height: String
    },

    render(h, context) {
        const {type, animation, className, tag, dark, square, bordered, size, width, height} = context.props
        const style = size !== undefined ? {width: size, height: size} : {width, height}
        const classes = `q-skeleton--${dark === true ? 'dark' : 'light'} q-skeleton--type-${type}` +
            (animation !== 'none' ? ` q-skeleton--anim-${animation}` : '') +
            (square === true ? ' q-skeleton--square' : '') +
            (bordered === true ? ' q-skeleton--bordered' : '') +
            (className !== undefined ? ' ' + className : '')

        return h(tag, {
            staticClass: 'q-skeleton',
            class: classes,
            style,
            on: context.$listeners
        }, context.children)
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
