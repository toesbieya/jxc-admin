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
                <abstract-form-item label="采购订单：" prop="pid">
                    <el-input v-if="canSave" :value="form.pid" readonly>
                        <el-button slot="append" @click="parentDialog=true">选择</el-button>
                    </el-input>
                    <template v-else>{{ form.pid }}</template>
                </abstract-form-item>
            </collapse-card>

            <collapse-card header="入库商品">
                <abstract-table :data="form.data" :highlight-current-row="false">
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
                            <span v-else>{{ row.num }}</span>
                        </template>
                    </el-table-column>
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

        <order-selector v-model="parentDialog" @select="selectParent"/>
    </div>
</template>

<script>
import docDetailMixin from "@/mixins/docDetailMixin"
import OrderSelector from './components/OrderSelector'
import {baseUrl, add, commit, getById, pass, reject, update, withdraw} from "@/api/doc/purchase/inbound"
import {getSubById as getParentSubById} from "@/api/doc/purchase/order"
import {isEmpty} from "@/utils"
import {elAlert} from "@/utils/message"
import {isInteger} from "@/utils/validate"

export default {
    name: "purchaseInboundDetail",

    mixins: [docDetailMixin],

    components: {OrderSelector},

    data() {
        return {
            baseUrl,
            docName: '采购入库单',
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
                num: i.remainNum
            }))
        },
        changeInboundNum(nv, ov, row) {
            let parentSub = this.parentSubList.find(i => i.cid === row.cid)
            if (!parentSub || nv > parentSub.remainNum) {
                return elAlert(`${row.cname}的入库数量超出采购数量`, () => row.num = ov)
            }
        },
        validate() {
            if (this.type === 'edit' && isEmpty(this.form.id)) {
                return '属性缺失，请关闭弹窗刷新重试'
            }
            if (this.form.data.length <= 0) {
                return '入库商品不能为空'
            }

            let index = 1
            for (let sub of this.form.data) {
                if (isEmpty(sub.num)) {
                    return `第${index}个商品数量不能为空`
                }
                if (sub.num < 0 || !isInteger(sub.num)) {
                    return `第${index}个商品数量有误`
                }

                let parentSub = this.parentSubList.find(i => i.cid === sub.cid)

                if (!parentSub) {
                    return `第${index}个商品不在采购订单中`
                }
                if (parentSub.remainNum < sub.num) {
                    return `第${index}个商品的入库数量超出采购数量`
                }
                index++
            }
            return null
        }
    }
}
</script>
