<script type="text/jsx">
    import {debounce} from "@/utils"

    export default {
        name: "SearchForm",
        provide() {
            return {
                searchForm: this
            }
        },
        props: {
            labelWidth: {type: String, default: '120px'},
            sm: {type: Number, default: 12},// >=768px
            lg: {type: Number, default: 8}, // >=1200px
            xl: {type: Number, default: 6}  // >=1920px
        },
        data() {
            return {
                showCollapse: false,
                collapse: true,
                num: 0
            }
        },
        methods: {
            handleCollapse() {
                this.collapse = !this.collapse
            },
            getElementNumInRow() {
                const vw = document.body.clientWidth
                if (vw < 1200) return Math.floor(24 / this.sm)
                else if (vw < 1920) return Math.floor(24 / this.lg)
                return Math.floor(24 / this.xl)
            },
            resize() {
                const num = this.getElementNumInRow()
                this.num = num
                this.showCollapse = num < this.$slots.default.length
            }
        },
        mounted() {
            this.resize = debounce(this.resize)
            this.resize()
            window.addEventListener('resize', this.resize)
        },
        beforeDestroy() {
            window.removeEventListener('resize', this.resize)
        },
        render() {
            const slots = this.$slots.default
            const collapseChildren = []
            if (this.showCollapse) {
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
                    collapseSlot ? collapseSlot({collapse: this.collapse, handle: this.handleCollapse}) :
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
                    {this.showCollapse ? collapseChildren : ''}
                </el-form>
            )
        }
    }
</script>

<style lang="scss">
    .searchForm {
        margin-bottom: -15px;

        .searchForm__collapse {
            position: relative;
            bottom: 20px;
            margin: 0 auto;
            text-align: center;
        }
    }
</style>
