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
                <doc-steps :status="form.status" :finish="form.finish" :type="type"/>
            </collapse-card>

            <collapse-card ref="base" header="基础信息">
                <abstract-form-item label="客 户" prop="customerName">
                    <el-input v-if="canSave" :value="form.customerName" readonly>
                        <template v-slot:append>
                            <el-button @click="customerDialog=true">选择</el-button>
                        </template>
                    </el-input>
                    <template v-else>{{ form.customerName }}</template>
                </abstract-form-item>
            </collapse-card>

            <collapse-card ref="goods" header="销售商品">
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
                            <span v-else>{{ row.num }}</span>
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
                            <span v-else>{{ row.price }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column align="center" label="剩余未出库" prop="remainNum"/>
                    <el-table-column v-if="form.id" align="center" label="出库情况" width="120">
                        <template v-slot="{row}">
                            <span :class="{success:row.remainNum===0}" class="dot"/>
                            {{ row.remainNum === 0 ? '已全部出库' : '未全部出库' }}
                        </template>
                    </el-table-column>
                    <template v-if="canSave" v-slot:append>
                        <div class="table-add-btn">
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
                    </template>
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

        <stock-selector v-model="stockDialog" @select="selectStock"/>

        <customer-selector v-model="customerDialog" @select="selectCustomer"/>
    </detail-page>
</template>

<script>
import docDetailMixin from "@/mixin/docDetailMixin"
import CustomerSelector from './component/CustomerSelector'
import StockSelector from './component/StockSelector'
import {getById, add, update, commit, withdraw, pass, reject} from "@/api/doc/sell/order"
import {isEmpty} from "@/util"
import {mul, plus} from "@/util/math"
import {isInteger} from "@/util/validate"

export default {
    name: "sellOrderDetail",

    mixins: [docDetailMixin],

    components: {CustomerSelector, StockSelector},

    data() {
        this.docName = '销售订单'
        this.api = {getById, add, update, commit, withdraw, pass, reject}
        return {
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
            anchors: [
                {ref: 'progress', label: '流程进度'},
                {ref: 'base', label: '基础信息'},
                {ref: 'goods', label: '销售商品'},
                {ref: 'attachment', label: '附件'},
                {ref: 'remark', label: '备注'},
                {ref: 'history', label: '单据历史'}
            ],
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
                if (!isInteger(sub.num) || sub.num < 0) return `第${index}个商品数量有误`
                if (!isInteger(sub.price) || sub.price <= 0) return `第${index}个商品单价有误`
                this.form.total = plus(this.form.total, mul(sub.num, sub.price))
                index++
            }
            return null
        }
    }
}
</script>
