<script type="text/jsx">
import {isEmpty, deepClone} from "@/util"
import {getElementInnerWidth} from '@/util/browser'
import {clearableComponentTag, clearFormItem} from "@/util/element-ui/elForm"
import {findComponentByTag} from "@/util/vue"

export default {
    name: "SearchForm",

    model: {
        prop: 'model',
        event: 'reset'
    },

    props: {
        // 搜索表单变量对象
        model: Object,
        // 是否默认展开全部
        defaultExpand: { type: Boolean, default: true },

        // el-form原始属性
        labelWidth: String,
        labelPosition: { type: String, default: 'right' },
        labelSuffix: { type: String, default: '：' },

        // 每个搜索项之间的间隔，等同于el-row的gutter
        gutter: { type: Number, default: 20 },

        /*每个宽度下，一行能有多少个控件，需要是24的因数*/
        xs: {type: Number, default: 1}, // <768px
        sm: {type: Number, default: 2}, // >=768px
        md: {type: Number, default: 3}, // >=998px
        lg: {type: Number, default: 4}  // >=1200px
    },

    data(vm) {
        return {
            maxLabelLength: 0,            // 子级el-form-item中最长的label长度，中文占1，英文占0.5
            showCollapse: false,          // 是否需要折叠控制
            collapse: !vm.defaultExpand,  // 是否处于折叠状态
            num: 0,                       // 从第几个控件开始需要隐藏
            span: 8                       // 24等分下，每个栅格的宽度，这里是默认值
        }
    },

    computed: {
        innerLabelWith() {
            // 优先使用传入的props.labelWidth
            if (!isEmpty(this.labelWidth)) return this.labelWidth
            // 否则使用子级最长的label长度 + 固定后缀长度（固定为1）+ 1
            const fixedLength = isEmpty(this.labelSuffix) ? 0 : 1
            return `${this.maxLabelLength + fixedLength + 1}em`
        }
    },

    watch: {
        defaultExpand: {
            immediate: true,
            handler(v) {
                v && (this.collapse = false)
            }
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

            this.$slots.default.forEach(({componentInstance}) => {
                const item = findComponentByTag(componentInstance, tag => clearableComponentTag.includes(tag))
                item && clearFormItem(item)
            })

            //因为表单可能存在无清空功能的组件，所以传递一次初始值
            this.$emit('reset', deepClone(this.initialModel))
        },
        handleSubmit() {
            this.handleSearch()
        },

        getElementNumInRow() {
            const vw = getElementInnerWidth(this.$el.parentNode)

            if (vw < 768) return this.xs
            if (vw < 998) return this.sm
            if (vw < 1200) return this.md
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
                    <el-form-item label-width="0">
                        <el-button type="primary" size="small" on-click={this.handleSearch}>查 询</el-button>
                        <el-button type="dashed" size="small" plain on-click={this.handleReset}>重 置</el-button>
                        {this.showCollapse && (
                            <el-button
                                class="search-form-action-button"
                                type="text"
                                size="small"
                                on-click={this.handleCollapse}
                            >
                                {ctrl.t}
                                <v-icon icon={ctrl.i}/>
                            </el-button>
                        )}
                    </el-form-item>
                </div>
            )
        }
    },

    mounted() {
        //记录初始条件
        this.initialModel = deepClone(this.model)

        this.resize()
        this.resizeObserver = new ResizeObserver(this.resize)
        this.resizeObserver.observe(this.$el.parentNode)

        this.$once('hook:beforeDestroy', () => {
            if (this.resizeObserver) {
                this.resizeObserver.disconnect()
                this.resizeObserver = null
            }
        })
    },

    render() {
        const slots = this.$slots.default.filter(i => i.tag)
        const collapse = this.showCollapse && this.collapse

        this.maxLabelLength = Math.max(...slots.map(vnode => {
            const label = vnode.componentOptions?.propsData?.label
            if (isEmpty(label)) return 0

            // 计算label对应多少em，中文为1em，英文为0.5em
            let num = 0
            for (let i = 0; i < label.length; i++) {
                num += label.charCodeAt(i) > 127 ? 1 : 0.5
            }
            return num
        }))

        const display = collapse ? slots.slice(0, this.num) : slots
        const hidden = collapse ? slots.slice(this.num) : []

        return (
            <el-form
                class="search-form"
                label-position={this.labelPosition}
                label-width={this.innerLabelWith}
                label-suffix={this.labelSuffix}
                size="small"
                {...{ nativeOn: { submit: this.handleSubmit } }}
            >
                <el-row gutter={this.gutter}>
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
        text-align: right;

        &-button {
            padding-left: 0;

            .icon {
                margin-left: 0.5em;
            }
        }
    }

    > .el-row {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .el-col.hide {
        display: none;
    }
}
</style>
