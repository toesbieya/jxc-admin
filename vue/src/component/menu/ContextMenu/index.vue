<script type="text/jsx">
const Item = {
    functional: true,

    render(h, context) {
        return <li class="context-menu-item" {...context.data}>{context.children}</li>
    }
}

export default {
    name: "ContextMenu",

    components: {Item},

    props: {
        value: Boolean,
        items: Array,
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
            document.body[v ? 'addEventListener' : 'removeEventListener']('click', this.close)
            v && this.$nextTick(this.autoAdapt)
        },
        left(v) {
            v && this.autoAdaptLeft(v)
        },
        top(v) {
            v && this.autoAdaptTop(v)
        }
    },

    methods: {
        close() {
            this.$emit('input', false)
        },
        autoAdapt() {
            this.autoAdaptTop(this.top)
            this.autoAdaptLeft(this.left)
        },
        autoAdaptTop(v) {
            if (!this.value) return

            const elOffsetHeight = this.$el.offsetHeight

            if (elOffsetHeight > document.body.clientHeight - v && v > elOffsetHeight) {
                this.realTop = v - elOffsetHeight + 'px'
            }
            else this.realTop = v + 'px'
        },
        autoAdaptLeft(v) {
            if (!this.value) return

            const elOffsetWidth = this.$el.offsetWidth

            if (elOffsetWidth > document.body.clientWidth - v) {
                this.realLeft = v - elOffsetWidth + 'px'
            }
            else this.realLeft = v + 'px'
        }
    },

    render() {
        if (!Array.isArray(this.items)) return

        const style = {left: this.realLeft, top: this.realTop}

        return (
            <ul v-show={this.value} class="context-menu" style={style}>
                {this.items.map(i => (
                    i && <item on-click={i.click}>{i.content}</item>
                ))}
            </ul>
        )
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.context-menu {
    margin: 0;
    background: $menu-dark-background;
    z-index: 10;
    position: fixed;
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
