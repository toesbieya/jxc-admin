<template>
    <ul v-show="value" class="contextmenu" :style="{left:realLeft,top:realTop}">
        <slot/>
    </ul>
</template>

<script>
export default {
    name: "Contextmenu",

    props: {
        value: Boolean,
        left: Number,
        top: Number
    },

    data() {
        return {
            realLeft: '0px',
            realTop: '0px'
        }
    },

    watch: {
        value(v) {
            document.body[v ? 'addEventListener' : 'removeEventListener']('click', this.closeContextMenu)
            if (v) this.$nextTick(this.autoAdapt)
        },
        left(v) {
            if (!v) return
            this.autoAdaptLeft(v)
        },
        top(v) {
            if (!v) return
            this.autoAdaptTop(v)
        }
    },

    methods: {
        closeContextMenu() {
            this.$emit('input', false)
        },
        autoAdapt() {
            this.autoAdaptTop(this.top)
            this.autoAdaptLeft(this.left)
        },
        autoAdaptTop(v) {
            if (!this.value) return
            if (this.$el.offsetHeight > document.body.clientHeight - v && v > this.$el.offsetHeight) {
                this.realTop = v - this.$el.offsetHeight + 'px'
            }
            else this.realTop = v + 'px'
        },
        autoAdaptLeft(v) {
            if (!this.value) return
            if (this.$el.offsetWidth > document.body.clientWidth - v) {
                this.realLeft = v - this.$el.offsetWidth + 'px'
            }
            else this.realLeft = v + 'px'
        }
    }
}
</script>

<style lang="scss" scoped>
@import "~@/asset/style/variables.scss";

.contextmenu {
    margin: 0;
    background: $aside-menu-background;
    z-index: 10;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #ffffff;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, .3);

    &-item {
        margin: 0;
        padding: 7px 16px;
        cursor: pointer;

        &:hover {
            color: $--color-primary;
        }
    }
}
</style>
