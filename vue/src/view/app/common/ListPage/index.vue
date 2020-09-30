<script type="text/jsx">
import AbstractTable from "@/component/abstract/Table"
import AbstractPagination from "@/component/abstract/Pagination"
import SearchForm from "@/component/form/Search"

const renderSearchForm = (h, slot) => {
    return slot && <SearchForm>{slot()}</SearchForm>
}

const renderButtons = (h, buttons) => {
    return (
        <el-row class="button-group">
            {buttons.map(button => {
                if (!button) return
                const {icon, type, content, e} = button
                return <el-button icon={icon} size="small" type={type} on-click={e}>{content}</el-button>
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
        loading: Boolean,
        buttons: {type: Array, default: () => []},
        list: Object
    },

    render(h, context) {
        const {props: {loading, buttons, list}, scopedSlots} = context

        return (
            <el-card v-loading={loading}>
                {renderSearchForm(h, scopedSlots.searchForm)}
                {renderButtons(h, buttons)}
                {renderList(h, {...list, tableColumn: scopedSlots.tableColumn})}
            </el-card>
        )
    }
}
</script>
