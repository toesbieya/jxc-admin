<script type="text/jsx">
import AbstractTable from "@/component/abstract/Table"
import AbstractPagination from "@/component/abstract/Pagination"
import SearchForm from "@/component/form/Search"
import autoAdaptHeightMixin from "./autoAdaptHeightMixin"
import {isEmpty} from "@/util"

export default {
    name: "ListPage",

    mixins: [autoAdaptHeightMixin],

    components: {AbstractTable, AbstractPagination, SearchForm},

    props: {data: Object},

    methods: {
        renderSearchForm(config, slot) {
            return slot && <search-form {...config}>{slot()}</search-form>
        },
        renderButtons(buttons) {
            if (buttons.length > 0 && isEmpty(buttons[0].type)) {
                buttons[0].type = 'primary'
            }
            return (
                <el-row class="button-group">
                    {buttons.map(button => {
                        if (!button) return
                        const {icon, type, content, e} = button
                        return (
                            <el-button
                                icon={icon}
                                size="small"
                                plain={isEmpty(type)}
                                type={type || 'dashed'}
                                on-click={e}
                            >
                                {content}
                            </el-button>
                        )
                    })}
                </el-row>
            )
        },
        renderTableAndPagination({loading, table, pagination}) {
            return (
                <el-row v-loading={loading} class="table-container">
                    <abstract-table max-height={this.tableMaxHeight} {...table}/>
                    <abstract-pagination {...pagination}/>
                </el-row>
            )
        }
    },

    render() {
        const {data: {pageLoading, buttons, dataLoading, search, table, pagination}, $scopedSlots} = this

        if (!table.scopedSlots) {
            table.scopedSlots = {}
        }
        table.scopedSlots.default = $scopedSlots.tableColumn

        return (
            <el-card v-loading={pageLoading}>
                {this.renderSearchForm(search, $scopedSlots.searchForm)}
                {this.renderButtons(buttons)}
                {this.renderTableAndPagination({loading: dataLoading, table, pagination})}
                {$scopedSlots.default && $scopedSlots.default()}
            </el-card>
        )
    }
}
</script>
