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
                <abstract-form-item label="供应商" prop="sname">
                    <el-input v-if="canSave" :value="form.sname" readonly>
                        <template v-slot:append>
                            <el-button @click="supplierDialog = true">选择</el-button>
                        </template>
                    </el-input>
                    <template v-else>{{ form.sname }}</template>
                </abstract-form-item>
            </collapse-card>

            <collapse-card ref="goods" header="采购商品">
                <abstract-table :data="form.data" :highlight-current-row="false">
                    <el-table-column align="center" label="#" type="index" width="80"/>
                    <el-table-column align="center" label="商 品">
                        <template v-slot="{row}">
                            <category-selector
                                v-if="row._editable"
                                v-model="row.cid"
                                :selected="selectedCategories"
                                @get-name="row.cname=$event"
                            />
                            <span v-else>{{ row.cname }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column align="center" label="采购数量">
                        <template v-slot="{row}">
                            <el-input-number
                                v-if="row._editable"
                                v-model="row.num"
                                controls-position="right"
                                :min="0"
                                size="small"
                            />
                            <span v-else>{{ row.num }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column align="center" label="采购单价">
                        <template v-slot="{row}">
                            <el-input-number
                                v-if="row._editable"
                                v-model="row.price"
                                controls-position="right"
                                :min="0"
                                size="small"
                            />
                            <span v-else>{{ row.price }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column v-if="form.id" align="center" label="剩余未入库" prop="remainNum"/>
                    <el-table-column v-if="form.id" align="center" label="入库情况" width="120">
                        <template v-slot="{row}">
                            <span :class="{success:row.remainNum===0}" class="dot"/>
                            {{ row.remainNum === 0 ? '已全部入库' : '未全部入库' }}
                        </template>
                    </el-table-column>
                    <el-table-column v-if="canSave" align="center" label="操作" width="140">
                        <template v-if="canSave" v-slot="{row,$index}">
                            <el-button v-if="!row._editable" type="text" @click="row._editable=true">编辑</el-button>
                            <el-button v-else type="text" @click="row._editable=false">保存</el-button>
                            <el-button type="text" @click="() => delSub(row,$index)">删除</el-button>
                        </template>
                    </el-table-column>
                    <template v-if="canSave" v-slot:append>
                        <div class="table-add-btn">
                            <el-button
                                plain
                                type="dashed"
                                icon="el-icon-plus"
                                :disabled="!canAddSub"
                                @click="addSub"
                            >
                                添加采购商品
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

        <supplier-selector v-model="supplierDialog" @select="selectSupplier"/>
    </detail-page>
</template>

<script>
import docDetailMixin from "@/mixin/docDetailMixin"
import CategorySelector from './component/CategorySelector'
import SupplierSelector from './component/SupplierSelector'
import {getById, add, update, commit, withdraw, pass, reject} from "@/api/doc/purchase/order"
import {isEmpty} from "@/util"
import {mul, plus} from "@/util/math"
import {isInteger} from "@/util/validate"

export default {
    name: "purchaseOrderDetail",

    mixins: [docDetailMixin],

    components: {CategorySelector, SupplierSelector},

    data() {
        this.docName = '采购订单'
        this.api = {getById, add, update, commit, withdraw, pass, reject}
        return {
            form: {
                sid: null,
                sname: null,
                finish: 0,
                ftime: null,
                total: 0
            },
            rules: {
                sname: [{required: true, message: '供应商不能为空', trigger: 'change'}]
            },
            anchors: [
                {ref: 'progress', label: '流程进度'},
                {ref: 'base', label: '基础信息'},
                {ref: 'goods', label: '采购商品'},
                {ref: 'attachment', label: '附件'},
                {ref: 'remark', label: '备注'},
                {ref: 'history', label: '单据历史'}
            ],
            supplierDialog: false
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
        selectSupplier(row) {
            this.form.sid = row.id
            this.form.sname = row.name
        },
        addSub() {
            this.form.data.push({
                cid: null,
                cname: null,
                num: 0,
                price: 0,
                _editable: true
            })
        },
        delSub(row, index) {
            this.form.data.splice(index, 1)
        },
        validate() {
            if (this.form.data.length <= 0) {
                return '采购商品不能为空'
            }
            if (this.type === 'edit' && isEmpty(this.form.id)) {
                return '属性缺失，请关闭弹窗刷新重试'
            }
            let index = 1
            for (let sub of this.form.data) {
                if (isEmpty(sub.cid, sub.cname)) {
                    return `第${index}个商品分类不能为空`
                }
                if (isEmpty(sub.num)) {
                    return `第${index}个商品数量不能为空`
                }
                if (isEmpty(sub.price)) {
                    return `第${index + 1}个商品单价不能为空`
                }
                if (!isInteger(sub.num) || sub.num < 0) {
                    return `第${index}个商品数量有误`
                }
                if (!isInteger(sub.price) || sub.price <= 0) {
                    return `第${index}个商品单价有误`
                }
                this.form.total = plus(this.form.total, mul(sub.num, sub.price))
                index++
            }
            return null
        },
        modifyDataBeforeMerge(data) {
            data.data.forEach(item => item._editable = false)
        }
    }
}
</script>
