<script type="text/jsx">
import AbstractTable from "@/component/abstract/Table"
import AbstractPagination from "@/component/abstract/Pagination"
import SearchForm from "@/component/form/Search"
import {isEmpty} from "@/util"

const renderSearchForm = (h, config, slot) => {
    return slot && <SearchForm {...config}>{slot()}</SearchForm>
}

const renderButtons = (h, buttons) => {
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
}

const renderList = (h, {loading, tableConfig, tableColumn, paginationConfig}) => {
    return (
        <el-row v-loading={loading} class="table-container">
            <AbstractTable {...tableConfig}>{tableColumn()}</AbstractTable>
            <AbstractPagination {...paginationConfig}/>
        </el-row>
    )
}

export default {
    name: "ListPage",

    functional: true,

    props: {
        data: Object
    },

    render(h, context) {
        const {props: {data: {pageLoading, buttons, dataLoading, search, table, pagination}}, scopedSlots} = context

        return (
            <el-card v-loading={pageLoading}>
                {renderSearchForm(h, search, scopedSlots.searchForm)}
                {renderButtons(h, buttons)}
                {renderList(h, {
                    loading: dataLoading,
                    tableConfig: table,
                    tableColumn: scopedSlots.tableColumn,
                    paginationConfig: pagination
                })}
                {scopedSlots.default && scopedSlots.default()}
            </el-card>
        )
    }
}
</script>
