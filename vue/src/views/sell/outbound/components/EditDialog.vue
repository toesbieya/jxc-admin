<template>
    <dialog-form :loading="loading" :value="value" width="70%" @close="cancel" @open="open">
        <template slot="title">
            {{title}}
            <document-history :data="history" :type="type" @show="getHistory"/>
        </template>
        <el-form
                ref="form"
                :model="form"
                :rules="rules"
                label-position="right"
                label-width="100px"
                size="small"
                status-icon
        >
            <el-row :gutter="20">
                <dialog-form-item label="单 号：" prop="id">
                    <el-input :value="form.id" readonly/>
                </dialog-form-item>
                <dialog-form-item label="销售订单：" prop="pid">
                    <el-input :value="form.pid" readonly>
                        <el-button slot="append" :disabled="!canSave" @click="parentDialog=true">选择</el-button>
                    </el-input>
                </dialog-form-item>
                <dialog-form-item label="创建人：" prop="cname">
                    <el-input :value="form.cname" readonly/>
                </dialog-form-item>
                <dialog-form-item label="创建时间：" prop="ctime">
                    <el-date-picker :value="form.ctime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
                </dialog-form-item>
                <dialog-form-item label="审核人：" prop="vname">
                    <el-input :value="form.vname" readonly/>
                </dialog-form-item>
                <dialog-form-item label="审核时间：" prop="vtime">
                    <el-date-picker :value="form.vtime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
                </dialog-form-item>
                <dialog-form-item label="附 件：" full>
                    <upload-file
                            :file-list="form.imageList"
                            :disabled="!canSave"
                            @remove="removeUpload"
                            @success="uploadSuccess"
                    />
                </dialog-form-item>
                <dialog-form-item label="备 注：" prop="remark" full>
                    <el-input
                            v-model="form.remark"
                            :rows="4"
                            :readonly="!canSave"
                            maxlength="200"
                            show-word-limit
                            type="textarea"
                    />
                </dialog-form-item>
            </el-row>
        </el-form>
        <el-table :data="displayTableData">
            <el-table-column align="center" type="expand">
                <el-table slot-scope="{row}" :data="row.data" border show-summary :summary-method="summaryStock(row)">
                    <el-table-column align="center" label="#" type="index" width="80"/>
                    <el-table-column align="center">
                        <template slot="header">
                            出库数量
                            <el-button v-if="canSave" type="text" size="small" @click="openStock(row)">选择库存</el-button>
                        </template>
                        <template slot-scope="d">
                            <el-input-number
                                    v-if="canSave"
                                    v-model="d.row.num"
                                    :min="0"
                                    size="small"
                                    @change="(nv,ov)=>changeOutboundNum(nv,ov,d.row,row)"
                            />
                            <span v-else>{{d.row.num}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column align="center" label="采购订单" prop="cgddid" show-overflow-tooltip/>
                    <el-table-column align="center" label="采购入库" prop="cgrkid" show-overflow-tooltip/>
                </el-table>
            </el-table-column>
            <el-table-column align="center" label="#" type="index" width="80"/>
            <el-table-column align="center" label="商 品" prop="cname"/>
            <el-table-column align="center" label="出库数量" prop="num" width="150"/>
            <el-table-column align="center" label="订单剩余未出库数量" prop="remain_num" width="250"/>
        </el-table>
        <document-steps :status="form.status" :type="type"/>
        <order-selector v-model="parentDialog" @select="selectParent"/>
        <stock-selector v-model="stockDialog" :cid="row?row.cid:null" @select="selectStock"/>
        <template v-slot:footer>
            <span v-if="form.status===2" class="seal">已审核</span>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canSave" size="small" type="primary" @click="save">保 存</el-button>
            <el-button v-if="canCommit" size="small" type="primary" @click="commit">提 交</el-button>
            <el-button v-if="canWithdraw" size="small" type="danger" @click="withdraw">撤 回</el-button>
            <el-button v-if="canPass" size="small" type="success" @click="pass">通 过</el-button>
            <el-button v-if="canReject" size="small" type="danger" @click="reject">驳 回</el-button>
        </template>
    </dialog-form>
</template>

<script>
    import OrderSelector from "./OrderSelector"
    import StockSelector from "./StockSelector"
    import bizDocumentDialogMixin from "@/mixins/bizDocumentDialogMixin"
    import {getDetailById as getStockDetail} from "@/api/stock/current"
    import {getSubById as getParentSubById} from "@/api/sell/order"
    import {add, commit, getById, pass, reject, update, withdraw} from "@/api/sell/outbound"
    import {isEmpty, plus, sub} from "@/utils"
    import {isInteger} from "@/utils/validate"
    import {elAlert} from "@/utils/message"

    export default {
        name: "EditDialog",
        mixins: [bizDocumentDialogMixin],
        components: {OrderSelector, StockSelector},
        data() {
            return {
                documentName: '销售出库单',
                api: {
                    getById, add, update, commit, withdraw, pass, reject
                },
                form: {
                    pid: null
                },
                rules: {
                    pid: [{required: true, message: '请选择销售订单', trigger: 'change'}]
                },
                stockDialog: false,
                row: null,
                parentDialog: false,
                displayTableData: []
            }
        },
        methods: {
            afterInit() {
                if (this.type === 'add') return
                let ids = this.form.data.map(i => i.sid).join(',')
                return Promise.all([
                    getParentSubById(this.form.pid),
                    getStockDetail(ids)
                ])
                    .then(([r1, r2]) => this.buildDisplayTableData(r1, r2))
            },
            //根据销售订单的子表、销售出库的子表、对应的库存来构造展示列表
            buildDisplayTableData(orderSubList, stockList) {
                this.displayTableData = []
                let group = {}
                this.form.data.forEach(i => {
                    if (group[i.cid] === undefined) group[i.cid] = []
                    let stock = stockList.find(j => j.id === i.sid)
                    group[i.cid].push({
                        ...i,
                        cgddid: stock ? stock.cgddid : null,
                        cgrkid: stock ? stock.cgrkid : null,
                        max_num: stock ? stock.num : 0
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
                let remain_num = this.row.remain_num
                for (let stock of stocks) {
                    if (remain_num <= 0) break
                    let item = {
                        ...stock,
                        sid: stock.id,
                        pid: this.form.id,
                        max_num: stock.num,
                        num: stock.num > remain_num ? remain_num : stock.num
                    }
                    outboundNum = plus(outboundNum, item.num)
                    res.push(item)
                    remain_num -= stock.num
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
                if (nv > row.max_num) {
                    return elAlert(`${row.cname}的出库数量超出库存数量`, () => row.num = ov)
                }

                let outboundNum = displayTableRow.data.map(i => i.num)
                outboundNum = outboundNum.length === 0 ? 0 : plus(...outboundNum)

                if (!outboundNum) return

                if (outboundNum > displayTableRow.remain_num) {
                    let correctNum = sub(displayTableRow.remain_num, sub(outboundNum, nv))
                    if (correctNum < 0) correctNum = 0
                    else if (correctNum > row.max_num) correctNum = row.max_num
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
                    if (sub.num < 0 || !isInteger(sub.num)) return `第${index}个商品的出库数量有误`
                    index++
                }

                return null
            }
        }
    }
</script>
