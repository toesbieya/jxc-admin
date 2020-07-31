<template>
    <div v-loading="loading" class="detail-page">
        <doc-detail-header
                :title="this.title"
                :description="headerDescription"
                :extra="headerExtra"
                @close="close"
        />

        <abstract-form :model="form" :rules="rules">
            <collapse-card header="流程进度">
                <doc-steps :status="form.status" :type="type"/>
            </collapse-card>

            <collapse-card header="基础信息">
                <abstract-form-item label="客 户：" prop="customerName">
                    <el-input v-if="canSave" :value="form.customerName" readonly>
                        <el-button slot="append" @click="customerDialog=true">选择</el-button>
                    </el-input>
                    <template v-else>{{form.customerName}}</template>
                </abstract-form-item>
            </collapse-card>

            <collapse-card header="销售商品">
                <abstract-table :data="form.data" :highlight-current-row="false">
                    <el-table-column align="center" label="#" type="index" width="80"/>
                    <el-table-column align="center" label="商品" prop="cname"/>
                    <el-table-column align="center" label="销售数量">
                        <template v-slot="{row}">
                            <el-input-number
                                    v-if="canSave"
                                    v-model="row.num"
                                    controls-position="right"
                                    :min="0"
                                    size="small"
                            />
                            <span v-else>{{row.num}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column align="center" label="销售单价">
                        <template v-slot="{row}">
                            <el-input-number
                                    v-if="canSave"
                                    v-model="row.price"
                                    controls-position="right"
                                    :min="0"
                                    size="small"
                            />
                            <span v-else>{{row.price}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column align="center" label="剩余未出库" prop="remainNum"/>
                    <el-table-column v-if="form.id" align="center" label="出库情况" width="120">
                        <template v-slot="{row}">
                            <span :class="{success:row.remainNum===0}" class="dot"/>
                            {{row.remainNum===0?'已全部出库':'未全部出库'}}
                        </template>
                    </el-table-column>
                    <div v-if="canSave" slot="append" class="table-add-btn">
                        <el-button
                                plain
                                type="dashed"
                                icon="el-icon-plus"
                                :disabled="!canAddSub"
                                @click="stockDialog=true"
                        >
                            添加销售商品
                        </el-button>
                    </div>
                </abstract-table>
            </collapse-card>

            <collapse-card header="附件">
                <upload-file
                        :file-list="form.imageList"
                        :disabled="!canSave"
                        @remove="removeUpload"
                        @success="uploadSuccess"
                />
            </collapse-card>

            <collapse-card header="备注">
                <el-input
                        v-model="form.remark"
                        :rows="4"
                        :readonly="!canSave"
                        maxlength="200"
                        show-word-limit
                        type="textarea"
                />
            </collapse-card>
        </abstract-form>

        <doc-history :id="form.id"/>

        <doc-detail-footer>
            <el-button plain size="small" @click="close">关 闭</el-button>
            <el-button v-if="canSave" size="small" type="primary" @click="save">保 存</el-button>
            <el-button v-if="canCommit" size="small" type="primary" @click="commit">提 交</el-button>
            <el-button v-if="canWithdraw" size="small" type="danger" @click="withdraw">撤 回</el-button>
            <el-button v-if="canPass" size="small" type="success" @click="pass">通 过</el-button>
            <el-button v-if="canReject" size="small" type="danger" @click="reject">驳 回</el-button>
        </doc-detail-footer>

        <stock-selector v-model="stockDialog" @select="selectStock"/>

        <customer-selector v-model="customerDialog" @select="selectCustomer"/>
    </div>
</template>

<script>
    import bizDocDetailMixin from "@/mixins/bizDocDetailMixin"
    import CustomerSelector from './components/CustomerSelector'
    import StockSelector from './components/StockSelector'
    import {baseUrl, add, commit, getById, pass, reject, update, withdraw} from "@/api/document/sell/order"
    import {isEmpty} from "@/utils"
    import {mul, plus} from "@/utils/math"
    import {isInteger} from "@/utils/validate"

    export default {
        name: "sellOrderDetail",

        mixins: [bizDocDetailMixin],

        components: {CustomerSelector, StockSelector},

        data() {
            return {
                baseUrl,
                docName: '销售订单',
                api: {
                    getById, add, update, commit, withdraw, pass, reject
                },
                form: {
                    customerId: null,
                    customerName: null,
                    finish: 0,
                    ftime: null,
                    total: 0
                },
                rules: {
                    customerName: [{required: true, message: '客户不能为空', trigger: 'change'}]
                },
                customerDialog: false,
                stockDialog: false
            }
        },

        computed: {
            headerDescription() {
                const f = this.$options.filters.timestamp2Date
                return [
                    {label: '创建人：', content: this.form.cname},
                    {label: '创建时间：', content: f(this.form.ctime)},
                    {label: '审核人：', content: this.form.vname},
                    {label: '审核时间：', content: f(this.form.vtime)},
                    {label: '完成时间：', content: f(this.form.ftime)}
                ]
            },
            headerExtra() {
                return [
                    {title: '单据状态', content: this.getStatus(this.form.status)},
                    {title: '完成情况', content: this.getFinish(this.form.finish)}
                ]
            },
            selectedCategories() {
                return this.form.data.map(i => i.cid)
            },
            canAddSub() {
                return this.form.data.every(i => !i._editable)
            }
        },

        methods: {
            selectCustomer(row) {
                this.form.customerId = row.id
                this.form.customerName = row.name
                this.customerDialog = false
            },
            selectStock(stocks) {
                this.form.data = stocks.map(i => ({cid: i.cid, cname: i.cname, num: i.totalNum, price: 0}))
                this.stockDialog = false
            },
            addSub() {
                this.form.data.push({
                    cid: null,
                    cname: null,
                    num: 0,
                    price: 0,
                    remainNum: 0
                })
            },
            delSub(row, index) {
                this.form.data.splice(index, 1)
            },
            validate() {
                if (this.form.data.length <= 0) return '销售商品不能为空'
                if (this.type === 'edit' && isEmpty(this.form.id)) {
                    return '属性缺失，请关闭弹窗刷新重试'
                }
                let index = 1
                for (let sub of this.form.data) {
                    if (isEmpty(sub.cid, sub.cname)) return `第${index}个商品分类不能为空`
                    if (isEmpty(sub.num)) return `第${index}个商品数量不能为空`
                    if (isEmpty(sub.price)) return `第${index + 1}个商品单价不能为空`
                    if (sub.num < 0 || !isInteger(sub.num)) return `第${index}个商品数量有误`
                    if (sub.price <= 0 || !isInteger(sub.price)) return `第${index}个商品单价有误`
                    this.form.total = plus(this.form.total, mul(sub.num, sub.price))
                    index++
                }
                return null
            }
        }
    }
</script>
