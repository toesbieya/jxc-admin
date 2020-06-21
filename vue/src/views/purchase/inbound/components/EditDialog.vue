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
                <dialog-form-item v-if="form.id" label="单 号：" prop="id">
                    <el-input :value="form.id" readonly/>
                </dialog-form-item>
                <dialog-form-item label="采购订单：" prop="pid">
                    <el-input :value="form.pid" readonly>
                        <el-button slot="append" :disabled="!canSave" @click="parentDialog=true">选择</el-button>
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

        <el-table :data="form.data">
            <el-table-column align="center" label="#" type="index" width="80"/>
            <el-table-column align="center" label="商 品" prop="cname"/>
            <el-table-column align="center" label="入库数量" width="150">
                <template v-slot="{row}">
                    <el-input-number
                            v-if="canSave"
                            v-model="row.num"
                            controls-position="right"
                            :min="0"
                            size="small"
                            @change="(nv,ov)=>changeInboundNum(nv,ov,row)"
                    />
                    <span v-else>{{row.num}}</span>
                </template>
            </el-table-column>
        </el-table>

        <document-steps :status="form.status" :type="type"/>

        <template v-slot:footer>
            <span v-if="form.status===2" class="seal">已审核</span>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canSave" size="small" type="primary" @click="save">保 存</el-button>
            <el-button v-if="canCommit" size="small" type="primary" @click="commit">提 交</el-button>
            <el-button v-if="canWithdraw" size="small" type="danger" @click="withdraw">撤 回</el-button>
            <el-button v-if="canPass" size="small" type="success" @click="pass">通 过</el-button>
            <el-button v-if="canReject" size="small" type="danger" @click="reject">驳 回</el-button>
        </template>

        <order-selector v-model="parentDialog" @select="selectParent"/>
    </dialog-form>
</template>

<script>
    import OrderSelector from "./OrderSelector"
    import bizDocumentDialogMixin from "@/mixins/bizDocumentDialogMixin"
    import {add, commit, getById, pass, reject, update, withdraw} from "@/api/purchase/inbound"
    import {getSubById as getParentSubById} from "@/api/purchase/order"
    import {isEmpty} from "@/utils"
    import {isInteger} from "@/utils/validate"
    import {elAlert} from "@/utils/message"

    export default {
        name: "EditDialog",

        mixins: [bizDocumentDialogMixin],

        components: {OrderSelector},

        data() {
            return {
                documentName: '采购入库单',
                api: {
                    getById, add, update, commit, withdraw, pass, reject
                },
                form: {
                    pid: null
                },
                rules: {
                    pid: [{required: true, message: '请选择采购订单', trigger: 'change'}]
                },
                parentDialog: false,
                parentSubList: []
            }
        },

        methods: {
            afterInit() {
                if (this.type !== 'edit') return
                return getParentSubById(this.form.pid).then(data => this.parentSubList = data)
            },

            selectParent(id, sub) {
                this.parentSubList = sub
                this.form.pid = id
                this.form.data = sub.map(i => ({
                    pid: this.form.id,
                    cid: i.cid,
                    cname: i.cname,
                    num: i.remain_num
                }))
            },

            changeInboundNum(nv, ov, row) {
                let parentSub = this.parentSubList.find(i => i.cid === row.cid)
                if (!parentSub || nv > parentSub.remain_num) {
                    return elAlert(`${row.cname}的入库数量超出采购数量`, () => row.num = ov)
                }
            },

            validate() {
                if (this.type === 'edit' && isEmpty(this.form.id)) {
                    return '属性缺失，请关闭弹窗刷新重试'
                }
                if (this.form.data.length <= 0) return '入库商品不能为空'
                let index = 1
                for (let sub of this.form.data) {
                    if (isEmpty(sub.num)) return `第${index}个商品数量不能为空`
                    if (sub.num < 0 || !isInteger(sub.num)) return `第${index}个商品数量有误`
                    let parentSub = this.parentSubList.find(i => i.cid === sub.cid)
                    if (!parentSub) return `第${index}个商品不在采购订单中`
                    if (parentSub.remain_num < sub.num) return `第${index}个商品的入库数量超出采购数量`
                    index++
                }
                return null
            }
        }
    }
</script>
