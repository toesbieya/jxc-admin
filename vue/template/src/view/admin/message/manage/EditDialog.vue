<template>
    <abstract-dialog :loading="loading" :title="title" :value="value" width="50%" @close="cancel" @open="open">
        <abstract-form :model="form" :rules="rules">
            <abstract-form-item label="标 题" prop="title" thin>
                <el-input v-model="form.title" maxlength="100" :readonly="!canSave"/>
            </abstract-form-item>
            <abstract-form-item label="消息类型" prop="type" thin>
                <el-select v-model="form.type" :disabled="!canSave">
                    <el-option :value="0" label="通知提醒"/>
                    <el-option :value="1" label="系统公告"/>
                </el-select>
            </abstract-form-item>
            <abstract-form-item v-if="type!=='add'" label="创建人" thin>
                <el-input :value="form.cname" readonly/>
            </abstract-form-item>
            <abstract-form-item v-if="type!=='add'" label="创建时间" thin>
                <el-date-picker :value="form.ctime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
            </abstract-form-item>
            <abstract-form-item label="通知对象" prop="broadcast" thin>
                <el-select v-model="form.broadcast" :disabled="!canSave">
                    <el-option :value="false" label="指定用户"/>
                    <el-option :value="true" label="全体用户"/>
                </el-select>
            </abstract-form-item>
            <abstract-form-item v-if="!form.broadcast" label="选择用户" prop="recipient" thin>
                <user-selector v-model="form.recipient" :disabled="!canSave"/>
            </abstract-form-item>
            <abstract-form-item v-if="form.pname" label="发布人" thin>
                <el-input :value="form.pname" readonly/>
            </abstract-form-item>
            <abstract-form-item v-if="form.ptime" label="发布时间" thin>
                <el-date-picker :value="form.ptime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
            </abstract-form-item>
            <abstract-form-item v-if="form.status===2" label="撤回人" thin>
                <el-input :value="form.wname" readonly/>
            </abstract-form-item>
            <abstract-form-item v-if="form.status===2" label="撤回时间" thin>
                <el-date-picker :value="form.wtime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
            </abstract-form-item>
            <abstract-form-item label="内 容" full>
                <rich-text-editor v-model="form.content" :readonly="!canSave"/>
            </abstract-form-item>
        </abstract-form>

        <template v-slot:footer>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canSave" size="small" type="primary" @click="save">保 存</el-button>
            <el-button v-if="canPublish" size="small" type="primary" @click="publish">发 布</el-button>
            <el-button v-if="canWithdraw" size="small" type="danger" @click="withdraw">撤 回</el-button>
        </template>
    </abstract-dialog>
</template>

<script>
import dialogMixin from "@/mixin/dialogMixin"
import AbstractForm from "@/component/abstract/Form"
import AbstractFormItem from "@/component/abstract/Form/item"
import AbstractDialog from '@/component/abstract/Dialog'
import RichTextEditor from "@/component/editor/RichTextEditor"
import UserSelector from '@/component/biz/UserSelector/SimpleMultipleUserSelector'
import {add, update, publish, withdraw} from "@/api/message/manage"
import {isEmpty, mergeObj, resetObj} from '@/util'
import {auth} from "@/util/auth"
import {elAlert, elConfirm, elSuccess} from "@/util/message"

export default {
    name: "EditDialog",

    mixins: [dialogMixin],

    components: {AbstractForm, AbstractFormItem, AbstractDialog, RichTextEditor, UserSelector},

    props: {
        value: Boolean,
        type: {type: String, default: 'see'},
        data: {type: Object, default: () => ({})}
    },

    data() {
        return {
            loading: false,
            needSearch: false,
            form: {
                id: null,
                title: null,
                resume: null,
                content: null,
                type: 0,
                cname: null,
                ctime: null,
                pname: null,
                ptime: null,
                wname: null,
                wtime: null,
                status: 0,
                broadcast: false,
                recipient: []
            },
            rules: {
                title: [{required: true, message: '请输入标题', trigger: 'change'}],
                type: [{required: true, message: '请选择消息类型', trigger: 'change'}],
                broadcast: [{required: true, message: '请选择通知对象', trigger: 'change'}]
            }
        }
    },

    computed: {
        title() {
            if (isEmpty(this.type)) return ''
            switch (this.type) {
                case 'see':
                    return '查看消息'
                case 'add':
                    return '添加消息'
                case 'edit':
                    return '编辑消息'
            }
        },

        canSave() {
            //add模式有添加权限、edit模式有编辑权限且status=0
            return this.type === 'add' && auth(add.url)
                || this.form.status === 0 && this.type === 'edit' && auth(update.url)
        },
        canPublish() {
            //有发布权限、add模式或edit模式且status=0
            return auth(publish.url) && (this.type === 'add' || this.type === 'edit' && this.form.status === 0)
        },
        canWithdraw() {
            //有撤回权限、edit模式且status=1
            return auth(withdraw.url)
                && this.type === 'edit'
                && this.form.status === 1
        },
    },

    methods: {
        open() {
            if (this.type === 'add') return
            const recipient = (this.data.recipient || '').split(',').filter(Boolean).map(i => parseInt(i))
            mergeObj(this.form, {...this.data, recipient})
        },

        clearForm() {
            resetObj(this.form)
            this.needSearch = false
            this.loading = false
            this.$nextTick(() => this.$refs.form.clearValidate())
        },

        cancel() {
            this.closeDialog()
            this.needSearch && this.$emit('search')

            window.setTimeout(() => this.clearForm(), 200)
            this.loading = false
        },

        save() {
            if (this.loading) return
            this.$refs.form.validate(v => {
                if (!v) return
                this.loading = true
                const data = this.transformForm()
                const promise = this.type === 'add' ? add.request(data) : update.request(data)
                promise
                    .then(({data, msg}) => {
                        this.needSearch = true
                        elSuccess(msg)
                        if (this.type === 'add') {
                            this.form.id = data.id
                            this.form.cname = data.cname
                            this.form.ctime = data.ctime
                        }
                        this.$emit('update:type', 'edit')
                    })
                    .finally(() => this.loading = false)
            })
        },

        publish() {
            if (this.loading) return
            this.$refs.form.validate(v => {
                if (!v) return
                if (this.validate) {
                    let valid = this.validate()
                    if (!isEmpty(valid)) return elAlert(valid)
                }
                elConfirm('确认发布？')
                    .then(() => this.loading = true)
                    .then(() => publish.request(this.transformForm()))
                    .then(({data, msg}) => {
                        elSuccess(msg)
                        this.needSearch = true

                        this.form.id = data.id
                        this.form.cname = data.cname
                        this.form.ctime = data.ctime
                        this.form.pid = data.pid
                        this.form.pname = data.pname
                        this.form.ptime = data.ptime
                        this.form.status = data.status

                        this.$emit('update:type', 'edit')
                    })
                    .finally(() => this.loading = false)
            })
        },

        withdraw() {
            if (this.loading) return
            elConfirm('确认撤回？')
                .then(() => {
                    this.loading = true
                    return withdraw.request({id: this.form.id, title: this.form.title})
                })
                .then(({msg}) => {
                    elSuccess(msg)
                    this.needSearch = true
                    this.closeDialog()
                })
                .finally(() => this.loading = false)
        },

        transformForm() {
            const recipient =
                Array.isArray(this.form.recipient) && this.form.recipient.length > 0
                    ? this.form.recipient.join(',')
                    : null
            return {...this.form, recipient}
        }
    }
}
</script>
