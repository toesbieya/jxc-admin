<template>
    <div v-if="isExternal" v-on="$listeners" class="svg-external-icon svg-icon" :style="styleExternalIcon"/>
    <svg v-else v-on="$listeners" :class="className" class="svg-icon" aria-hidden="true">
        <use :href="iconName"/>
    </svg>
</template>

<script>
    import {isExternal} from '@/utils/validate'

    export default {
        name: 'SvgIcon',
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
        computed: {
            isExternal() {
                return isExternal(this.icon)
            },
            iconName() {
                return `#icon-${this.icon}`
            },
            styleExternalIcon() {
                return {
                    mask: `url(${this.icon}) no-repeat 50% 50%`,
                    '-webkit-mask': `url(${this.icon}) no-repeat 50% 50%`
                }
            }
        },
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
