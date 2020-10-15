<script type="text/jsx">
import {deepClone} from "@/util"
import {getElementInnerWidth} from '@/util/browser'

export default {
    name: "SearchForm",

    model: {
        prop: 'model',
        event: 'reset'
    },

    props: {
        model: Object,
        labelWidth: {type: String, default: '120px'},

        /*每个宽度下，一行能有多少个控件，需要是24的因数*/
        xs: {type: Number, default: 1}, // <768px
        sm: {type: Number, default: 2}, // >=768px
        md: {type: Number, default: 3}, // >=998px
        lg: {type: Number, default: 4}  // >=1200px
    },

    data() {
        return {
            showCollapse: false, //是否需要折叠控制
            collapse: true,      //是否处于折叠状态，默认折叠
            num: 0,              //从第几个控件开始需要隐藏
            span: 8              //24等分下，每个栅格的宽度
        }
    },

    methods: {
        handleCollapse() {
            this.collapse = !this.collapse
        },

        handleSearch() {
            this.$emit('search')
        },

        handleReset() {
            if (!this.initialModel || !this.model) {
                return
            }

            this.$emit('reset', deepClone(this.initialModel))
        },

        getElementNumInRow() {
            const vw = getElementInnerWidth(this.$el.parentNode)

            if (vw < 768) return this.xs
            else if (vw < 998) return this.sm
            else if (vw < 1200) return this.md
            return this.lg
        },

        resize() {
            const num = this.getElementNumInRow()

            this.span = 24 / num

            //考虑后面的按钮组的占位
            this.num = num === 1 ? num : num - 1
            this.showCollapse = this.num < this.$slots.default.length
        },

        renderChildren(children, hide) {
            return children.map(child => (
                <el-col span={this.span} class={{hide}}>{child}</el-col>
            ))
        },

        renderAction() {
            const ctrl = this.collapse
                ? {i: 'el-icon-arrow-down', t: '展开'}
                : {i: 'el-icon-arrow-up', t: '收起'}

            return (
                <div class="search-form-action">
                    <el-button type="primary" size="small" on-click={this.handleSearch}>查 询</el-button>
                    <el-button type="dashed" size="small" plain on-click={this.handleReset}>重 置</el-button>
                    {this.showCollapse && (
                        <el-button
                            type="text"
                            size="small"
                            style="padding-left: 0"
                            on-click={this.handleCollapse}
                        >
                            {ctrl.t}
                            <v-icon icon={ctrl.i} style="margin-left: 0.5em"/>
                        </el-button>
                    )}
                </div>
            )
        }
    },

    mounted() {
        //记录初始条件
        this.initialModel = deepClone(this.model)

        this.resize()
        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.$el.parentNode)

        this.$once('hook:beforeDestroy', () => {
            if (this.resizeObserver) {
                this.resizeObserver.disconnect()
                this.resizeObserver = null
            }
        })
    },

    render() {
        const slots = this.$slots.default, collapse = this.showCollapse && this.collapse

        const display = collapse ? slots.slice(0, this.num) : slots
        const hidden = collapse ? slots.slice(this.num) : []

        return (
            <el-form
                class="search-form"
                label-position="right"
                label-width={this.labelWidth}
                label-suffix=":"
                size="small"
            >
                <el-row gutter={20}>
                    {this.renderChildren(display)}
                    {this.renderChildren(hidden, true)}
                    {this.renderAction()}
                </el-row>
            </el-form>
        )
    }
}
</script>

<style lang="scss">
.search-form {
    &-action {
        margin-left: auto;
    }

    > .el-row {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .hide {
        display: none;
    }
}
</style>
