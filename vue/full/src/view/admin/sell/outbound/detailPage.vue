<template>
    <detail-page
        :loading="loading"
        :reference="() => $refs"
        :anchors="anchors"
        :title="title"
        :description="headerDescription"
        :extra="headerExtra"
        :buttons="buttons"
        @close="close"
    >
        <abstract-form :model="form" :rules="rules">
            <collapse-card ref="progress" header="流程进度">
                <doc-steps :status="form.status" :type="type"/>
            </collapse-card>

            <collapse-card ref="base" header="基础信息">
                <abstract-form-item label="销售订单" prop="pid">
                    <el-input v-if="canSave" :value="form.pid" readonly>
                        <template v-slot:append>
                            <el-button @click="parentDialog=true">选择</el-button>
                        </template>
                    </el-input>
                    <template v-else>{{ form.pid }}</template>
                </abstract-form-item>
            </collapse-card>

            <collapse-card ref="goods" header="出库商品">
                <abstract-table :data="displayTableData" :highlight-current-row="false">
                    <el-table-column align="center" type="expand">
                        <el-table slot-scope="{row}" :data="row.data" border show-summary
                                  :summary-method="summaryStock(row)">
                            <el-table-column align="center" label="#" type="index" width="80"/>
                            <el-table-column align="center">
                                <template v-slot:header>
                                    出库数量
                                    <el-button
                                        v-if="canSave"
                                        type="text"
                                        size="small"
                                        @click="openStock(row)"
                                    >
                                        选择库存
                                    </el-button>
                                </template>
                                <template v-slot="d">
                                    <el-input-number
                                        v-if="canSave"
                                        v-model="d.row.num"
                                        controls-position="right"
                                        :min="0"
                                        size="small"
                                        @change="(nv,ov)=>changeOutboundNum(nv,ov,d.row,row)"
                                    />
                                    <span v-else>{{ d.row.num }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column align="center" label="采购订单" prop="cgddid" show-overflow-tooltip/>
                            <el-table-column align="center" label="采购入库" prop="cgrkid" show-overflow-tooltip/>
                        </el-table>
                    </el-table-column>
                    <el-table-column align="center" label="#" type="index" width="80"/>
                    <el-table-column align="center" label="商 品" prop="cname"/>
                    <el-table-column align="center" label="出库数量" prop="num" width="150"/>
                    <el-table-column align="center" label="订单剩余未出库数量" prop="remainNum" width="250"/>
                </abstract-table>
            </collapse-card>

            <collapse-card ref="attachment" header="附件">
                <qiniu-upload
                    :file-list="form.imageList"
                    :disabled="!canSave"
                    @remove="removeUpload"
                    @success="uploadSuccess"
                />
            </collapse-card>

            <collapse-card ref="remark" header="备注">
                <el-input
                    v-model="form.remark"
                    :rows="4"
                    :readonly="!canSave"
                    maxlength="200"
                    show-word-limit
                    type="textarea"
                />
            </collapse-card>

            <collapse-card ref="history" header="单据历史">
                <doc-history :id="form.id"/>
            </collapse-card>
        </abstract-form>

        <order-selector v-model="parentDialog" @select="selectParent"/>

        <stock-selector v-model="stockDialog" :cid="row ? row.cid : null" @select="selectStock"/>
    </detail-page>
</template>

<script>
import docDetailMixin from "@/mixin/docDetailMixin"
import OrderSelector from "./component/OrderSelector"
import StockSelector from "./component/StockSelector"
import {getDetailById as getStockDetail} from "@/api/stock/current"
import {getSubById as getParentSubById} from "@/api/doc/sell/order"
import {getById, add, update, commit, withdraw, pass, reject} from "@/api/doc/sell/outbound"
import {isEmpty} from "@/util"
import {plus, sub} from "@/util/math"
import {elAlert} from "@/util/message"
import {isInteger} from "@/util/validate"

export default {
    name: "sellOutboundDetail",

    mixins: [docDetailMixin],

    components: {OrderSelector, StockSelector},

    data() {
        this.docName = '销售出库单'
        this.api = {getById, add, update, commit, withdraw, pass, reject}
        return {
            form: {
                pid: null
            },
            rules: {
                pid: [{required: true, message: '请选择销售订单', trigger: 'change'}]
            },
            anchors: [
                {ref: 'progress', label: '流程进度'},
                {ref: 'base', label: '基础信息'},
                {ref: 'goods', label: '出库商品'},
                {ref: 'attachment', label: '附件'},
                {ref: 'remark', label: '备注'},
                {ref: 'history', label: '单据历史'}
            ],
            stockDialog: false,
            row: null,
            parentDialog: false,
            displayTableData: []
        }
    },

    computed: {
        headerDescription() {
            const f = this.$options.filters.timestamp2Date
            return [
                {label: '创建人：', content: this.form.cname},
                {label: '创建时间：', content: f(this.form.ctime)},
                {label: '审核人：', content: this.form.vname},
                {label: '审核时间：', content: f(this.form.vtime)}
            ]
        },
        headerExtra() {
            return [
                {title: '单据状态', content: this.getStatus(this.form.status)}
            ]
        }
    },

    methods: {
        afterInit() {
            if (this.type === 'add') return Promise.resolve()
            const ids = this.form.data.map(i => i.sid).join(',')
            return Promise.all([
                getParentSubById.request(this.form.pid),
                getStockDetail.request(ids)
            ])
                .then(([r1, r2]) => this.buildDisplayTableData(r1.data, r2.data))
        },

        //根据销售订单的子表、销售出库的子表、对应的库存来构造展示列表
        buildDisplayTableData(orderSubList, stockList) {
            this.displayTableData = []
            const group = {}
            this.form.data.forEach(i => {
                if (group[i.cid] === undefined) group[i.cid] = []
                let stock = stockList.find(j => j.id === i.sid)
                group[i.cid].push({
                    ...i,
                    cgddid: stock ? stock.cgddid : null,
                    cgrkid: stock ? stock.cgrkid : null,
                    maxNum: stock ? stock.num : 0
                })
            })
            orderSubList.forEach(i => {
                if (group[i.cid]) {
                    let num = plus(...group[i.cid].map(j => j.num))
                    this.displayTableData.push({...i, num, data: group[i.cid]})
                    delete group[i.cid]
                }
            })
            if (Object.keys(group).length > 0) {
                elAlert('出库商品不符合销售订单，请重新选择', () => {
                    this.displayTableData = []
                    this.form.data = []
                })
            }
        },

        selectParent(id, sub) {
            this.form.pid = id
            this.displayTableData = sub.map(i => ({...i, num: 0, data: []}))
        },

        openStock(row) {
            this.row = row
            this.stockDialog = true
        },

        selectStock(stocks) {
            let res = []
            let outboundNum = 0
            let remainNum = this.row.remainNum
            for (let stock of stocks) {
                if (remainNum <= 0) break
                let item = {
                    ...stock,
                    sid: stock.id,
                    pid: this.form.id,
                    maxNum: stock.num,
                    num: stock.num > remainNum ? remainNum : stock.num
                }
                outboundNum = plus(outboundNum, item.num)
                res.push(item)
                remainNum -= stock.num
            }
            this.row.num = outboundNum
            this.row.data = res
            this.stockDialog = false
        },

        summaryStock(row) {
            return ({data}) => {
                let sum = ['合计', 0, '', '']
                data.forEach(i => {
                    sum[1] = plus(sum[1], i.num)
                })
                row.num = sum[1]
                return sum
            }
        },

        changeOutboundNum(nv, ov, row, displayTableRow) {
            if (nv > row.maxNum) {
                return elAlert(`${row.cname}的出库数量超出库存数量`, () => row.num = ov)
            }

            let outboundNum = displayTableRow.data.map(i => i.num)
            outboundNum = outboundNum.length === 0 ? 0 : plus(...outboundNum)

            if (!outboundNum) return

            if (outboundNum > displayTableRow.remainNum) {
                let correctNum = sub(displayTableRow.remainNum, sub(outboundNum, nv))
                if (correctNum < 0) correctNum = 0
                else if (correctNum > row.maxNum) correctNum = row.maxNum
                return elAlert(`${displayTableRow.cname}的出库数量超出销售数量`, () => row.num = ov)
            }
        },

        validate() {
            if (this.type === 'edit' && isEmpty(this.form.id)) {
                return '属性缺失，请关闭弹窗刷新重试'
            }

            this.form.data = this.displayTableData.reduce((arr, i) => arr.concat(i.data), [])

            if (this.form.data.length <= 0) return '出库商品不能为空'

            let index = 1
            for (let sub of this.form.data) {
                if (isEmpty(sub.num)) return `第${index}个商品的出库数量不能为空`
                if (!isInteger(sub.num) || sub.num < 0) return `第${index}个商品的出库数量有误`
                index++
            }

            return null
        }
    }
}
</script>
