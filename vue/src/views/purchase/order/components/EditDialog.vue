<template>
    <dialog-form :loading="loading" :value="value" width="70%" @close="cancel" @open="open">
        <template v-slot:title>
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
                <dialog-form-item v-if="form.id" label="单 号：" prop="id">
                    <el-input :value="form.id" readonly/>
                </dialog-form-item>
                <dialog-form-item label="供应商：" prop="sname">
                    <el-input :value="form.sname" readonly>
                        <el-button slot="append" :disabled="!canSave" @click="supplierDialog=true">选择</el-button>
                    </el-input>
                </dialog-form-item>
                <dialog-form-item v-if="form.id" label="创建人：" prop="cname">
                    <el-input :value="form.cname" readonly/>
                </dialog-form-item>
                <dialog-form-item v-if="form.id" label="创建时间：" prop="ctime">
                    <el-date-picker :value="form.ctime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
                </dialog-form-item>
                <dialog-form-item v-if="form.status===2" label="审核人：" prop="vname">
                    <el-input :value="form.vname" readonly/>
                </dialog-form-item>
                <dialog-form-item v-if="form.status===2" label="审核时间：" prop="vtime">
                    <el-date-picker :value="form.vtime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
                </dialog-form-item>
                <dialog-form-item v-if="form.finish===2" label="完成时间：" prop="ftime">
                    <el-date-picker :value="form.ftime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
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

        <el-table ref="table" :data="form.data">
            <el-table-column align="center" label="#" type="index" width="80"/>
            <el-table-column align="center" label="商 品">
                <template v-slot="{row}">
                    <category-selector
                            v-if="row._editable"
                            v-model="row.cid"
                            :selected="selectedCategories"
                            @get-name="row.cname=$event"
                    />
                    <span v-else>{{row.cname}}</span>
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
                    <span v-else>{{row.num}}</span>
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
                    <span v-else>{{row.price}}</span>
                </template>
            </el-table-column>
            <el-table-column v-if="form.id" align="center" label="剩余未入库" prop="remain_num"/>
            <el-table-column v-if="form.id" align="center" label="入库情况" width="120">
                <template v-slot="{row}">
                    <span :class="{success:row.remain_num===0}" class="dot"/>
                    {{row.remain_num===0?'已全部入库':'未全部入库'}}
                </template>
            </el-table-column>
            <el-table-column v-if="canSave" align="center" label="操作" width="140">
                <template v-if="canSave" v-slot="{row,$index}">
                    <el-button v-if="!row._editable" type="text" @click="()=>row._editable=true">编辑</el-button>
                    <el-button v-else type="text" @click="()=>row._editable=false">保存</el-button>
                    <el-button type="text" @click="()=>delSub(row,$index)">删除</el-button>
                </template>
            </el-table-column>
            <div v-if="canSave" slot="append" class="table-add-btn">
                <el-button plain type="dashed" size="small" icon="el-icon-plus" @click="addSub">添加采购商品</el-button>
            </div>
        </el-table>

        <document-steps :status="form.status" :type="type"/>

        <template v-slot:footer>
            <span v-if="form.status===2" class="seal">已审核</span>
            <span v-if="form.finish===2" class="seal">已完成</span>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canSave" size="small" type="primary" @click="save">保 存</el-button>
            <el-button v-if="canCommit" size="small" type="primary" @click="commit">提 交</el-button>
            <el-button v-if="canWithdraw" size="small" type="danger" @click="withdraw">撤 回</el-button>
            <el-button v-if="canPass" size="small" type="success" @click="pass">通 过</el-button>
            <el-button v-if="canReject" size="small" type="danger" @click="reject">驳 回</el-button>
        </template>

        <supplier-selector v-model="supplierDialog" @select="selectSupplier" @jump="closeDialog"/>
    </dialog-form>
</template>

<script>
    import SupplierSelector from './SupplierSelector'
    import CategorySelector from './CategorySelector'
    import bizDocumentDialogMixin from "@/mixins/bizDocumentDialogMixin"
    import {add, commit, getById, pass, reject, update, withdraw} from "@/api/document/purchase/order"
    import {isEmpty} from "@/utils"
    import {plus, mul} from "@/utils/math"
    import {isInteger} from "@/utils/validate"

    export default {
        name: "EditDialog",
        mixins: [bizDocumentDialogMixin],
        components: {CategorySelector, SupplierSelector},
        data() {
            return {
                documentName: '采购订单',
                api: {
                    getById, add, update, commit, withdraw, pass, reject
                },
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
                supplierDialog: false
            }
        },
        computed: {
            selectedCategories() {
                return this.form.data.map(i => i.cid)
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
                if (this.form.data.length <= 0) return '采购商品不能为空'
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
            },
            afterInit() {
                this.form.data.forEach(item => item.__editable = false)
            }
        }
    }
</script>
