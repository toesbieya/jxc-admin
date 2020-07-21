<script type="text/jsx">
    import {getElementInnerWidth} from '@/utils/browser'

    export default {
        name: "SearchForm",

        provide() {
            return {
                searchForm: this
            }
        },

        props: {
            labelWidth: {type: String, default: '120px'},
            xs: {type: Number, default: 1},// <768px
            sm: {type: Number, default: 2},// >=768px
            md: {type: Number, default: 3}, // >=998px
            lg: {type: Number, default: 4}  // >=1200px
        },

        data() {
            return {
                showCollapse: false,
                collapse: true,
                num: 0,
                width: 'auto'
            }
        },

        methods: {
            handleCollapse() {
                this.collapse = !this.collapse
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
                this.num = num
                this.width = `${100 / num}%`
                this.showCollapse = num < this.$slots.default.length
            }
        },

        mounted() {
            this.resize()

            window.addEventListener('resize', this.resize)

            this.$once('hook:beforeDestroy', () => {
                window.removeEventListener('resize', this.resize)
            })
        },

        render() {
            const slots = this.$slots.default
            const collapseChildren = []
            if (this.showCollapse) {
                //此处直接对el-row使用v-show会无效
                collapseChildren.push(
                    <el-collapse-transition>
                        <div v-show={!this.collapse}>
                            <el-row gutter={20}>
                                {slots.slice(this.num)}
                            </el-row>
                        </div>
                    </el-collapse-transition>
                )
                const collapseSlot = this.$scopedSlots.collapse

                collapseChildren.push(
                    collapseSlot ?
                        collapseSlot({collapse: this.collapse, handle: this.handleCollapse})
                        :
                        <div class="searchForm__collapse">
                            <el-button
                                type="text"
                                size="mini"
                                icon={this.collapse ? 'el-icon-arrow-down' : 'el-icon-arrow-up'}
                                on-click={this.handleCollapse}
                            >
                                {this.collapse ? '展开' : '收起'}
                            </el-button>
                        </div>
                )
            }

            return (
                <el-form class="searchForm" label-position="right" label-width={this.labelWidth} size="small">
                    <el-row gutter={20}>
                        {this.num > slots.length ? slots : slots.slice(0, this.num)}
                    </el-row>
                    {this.showCollapse && collapseChildren}
                </el-form>
            )
        }
    }
</script>

<style lang="scss">
    .searchForm {
        margin-bottom: -15px;

        > .el-row {
            display: flex;
            flex-direction: row;
            flex-wrap: wrap;
        }

        .searchForm__collapse {
            position: relative;
            bottom: 20px;
            margin: 0 auto;
            text-align: center;
        }
    }
</style>
