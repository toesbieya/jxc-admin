<template>
    <list-page :data="listPageConfig">
        <template v-slot:searchForm>
            <el-form-item label="单 号">
                <el-input v-model="searchForm.idFuzzy" clearable maxlength="50"/>
            </el-form-item>
            <el-form-item label="销售订单">
                <el-input v-model="searchForm.pidFuzzy" clearable maxlength="50"/>
            </el-form-item>
            <el-form-item label="创建人">
                <el-input v-model="searchForm.cname" clearable maxlength="50"/>
            </el-form-item>
            <el-form-item label="创建时间">
                <el-date-picker
                    v-model="temp.ctime"
                    format="yyyy-MM-dd"
                    range-separator="-"
                    type="daterange"
                    value-format="timestamp"
                />
            </el-form-item>
            <el-form-item label="审核人">
                <el-input v-model="searchForm.vname" clearable maxlength="50"/>
            </el-form-item>
            <el-form-item label="审核时间">
                <el-date-picker
                    v-model="temp.vtime"
                    format="yyyy-MM-dd"
                    range-separator="-"
                    type="daterange"
                    value-format="timestamp"
                />
            </el-form-item>
            <el-form-item label="状 态">
                <el-select v-model="temp.status" clearable multiple>
                    <el-option :value="0" label="拟定"/>
                    <el-option :value="1" label="待审核"/>
                    <el-option :value="2" label="已审核"/>
                </el-select>
            </el-form-item>
        </template>
        <template v-slot:tableColumn>
            <el-table-column align="center" type="expand">
                <template v-slot="{row}">
                    <liner-progress :show="row._loading"/>
                    <el-table v-show="!row._loading" :data="row.data" border row-key="id">
                        <el-table-column align="center" label="#" type="index" width="80"/>
                        <el-table-column align="center" label="商品" prop="cname" show-overflow-tooltip/>
                        <el-table-column align="center" label="出库数量" prop="num"/>
                    </el-table>
                </template>
            </el-table-column>
            <el-table-column align="center" label="#" type="index" width="80"/>
            <el-table-column align="center" label="单 号" width="160">
                <router-link
                    slot-scope="{row}"
                    :to="`/sell/outbound/detail/see/${row.id}`"
                    @click.native.stop
                >
                    {{ row.id }}
                </router-link>
            </el-table-column>
            <el-table-column align="center" label="创建人" prop="cname" show-overflow-tooltip/>
            <el-table-column align="center" label="创建时间" width="150">
                <template v-slot="{row}">{{ row.ctime | timestamp2Date }}</template>
            </el-table-column>
            <el-table-column align="center" label="审核人" prop="vname" show-overflow-tooltip/>
            <el-table-column align="center" label="审核时间" width="150">
                <template v-slot="{row}">{{ row.vtime | timestamp2Date }}</template>
            </el-table-column>
            <el-table-column align="center" label="状 态" width="120">
                <template v-slot="{row}">
                    <span :class="{primary:row.status===1,success:row.status===2}" class="dot"/>
                    {{ getStatus(row.status) }}
                </template>
            </el-table-column>
        </template>
    </list-page>
</template>

<script>
import docTableMixin from '@/mixin/docTableMixin'
import {add, update, del, withdraw, pass, reject, getSubById, search, exportExcel} from "@/api/doc/sell/outbound"

export default {
    name: "sellOutbound",

    mixins: [docTableMixin],

    data() {
        this.api = {add, update, del, withdraw, pass, reject, getSubById, search, exportExcel}
        return {
            searchForm: {
                pidFuzzy: null
            },
            excel: {
                columns: [
                    {header: '序号', prop: 'id'},
                    {header: '销售订单单号', prop: 'pid', width: 30},
                    {header: '创建人', prop: 'cname'},
                    {header: '创建时间', prop: 'ctime'},
                    {header: '审核人', prop: 'vname'},
                    {header: '审核时间', prop: 'vtime'},
                    {header: '状态', prop: 'status'},
                    {header: '备注', prop: 'remark', width: 50}
                ]
            }
        }
    },

    methods: {
        mergeSearchForm() {
            return {
                ...this.searchForm,
                status: this.temp.status.join(','),
                ctimeStart: this.temp.ctime ? this.temp.ctime[0] : null,
                ctimeEnd: this.temp.ctime ? this.temp.ctime[1] + 86400000 : null,
                vtimeStart: this.temp.vtime ? this.temp.vtime[0] : null,
                vtimeEnd: this.temp.vtime ? this.temp.vtime[1] + 86400000 : null,
            }
        }
    }
}
</script>
