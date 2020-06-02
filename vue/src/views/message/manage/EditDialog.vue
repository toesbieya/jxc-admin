<template>
    <dialog-form :loading="loading" :title="title" :value="value" width="50%" @close="cancel" @open="open">
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
                <dialog-form-item label="标 题：" prop="title" dense>
                    <el-input v-model="form.title" maxlength="100" :readonly="!canSave"/>
                </dialog-form-item>
                <dialog-form-item label="消息类型：" prop="type" dense>
                    <el-select v-model="form.type" :disabled="!canSave">
                        <el-option :value="0" label="通知提醒"/>
                        <el-option :value="1" label="系统公告"/>
                    </el-select>
                </dialog-form-item>
                <dialog-form-item v-if="type!=='add'" label="创建人：" dense>
                    <el-input :value="form.cname" readonly/>
                </dialog-form-item>
                <dialog-form-item v-if="type!=='add'" label="创建时间：" dense>
                    <el-date-picker :value="form.ctime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
                </dialog-form-item>
                <dialog-form-item label="通知对象：" prop="all" dense>
                    <el-select v-model="form.all" :disabled="!canSave">
                        <el-option :value="0" label="指定用户"/>
                        <el-option :value="1" label="全体用户"/>
                    </el-select>
                </dialog-form-item>
                <dialog-form-item v-if="form.all===0" label="选择用户：" prop="recipient" dense>
                    <user-selector v-model="form.recipient" :disabled="!canSave"/>
                </dialog-form-item>
                <dialog-form-item v-if="form.pname" label="发布人：" dense>
                    <el-input :value="form.pname" readonly/>
                </dialog-form-item>
                <dialog-form-item v-if="form.ptime" label="发布时间：" dense>
                    <el-date-picker :value="form.ptime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
                </dialog-form-item>
                <dialog-form-item v-if="form.status===2" label="撤回人：" dense>
                    <el-input :value="form.wname" readonly/>
                </dialog-form-item>
                <dialog-form-item v-if="form.status===2" label="撤回时间：" dense>
                    <el-date-picker :value="form.wtime" format="yyyy-MM-dd HH:mm:ss" readonly type="date"/>
                </dialog-form-item>
                <dialog-form-item label="内 容：" full>
                    <tinymce-editor v-model="form.content" :readonly="!canSave"/>
                </dialog-form-item>
            </el-row>
        </el-form>
        <template v-slot:footer>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button v-if="canSave" size="small" type="primary" @click="save">保 存</el-button>
            <el-button v-if="canPublish" size="small" type="primary" @click="publish">发 布</el-button>
            <el-button v-if="canWithdraw" size="small" type="danger" @click="withdraw">撤 回</el-button>
        </template>
    </dialog-form>
</template>

<script>
    import DialogForm from '@/components/DialogForm'
    import DialogFormItem from "@/components/DialogForm/DialogFormItem"
    import TinymceEditor from "@/components/TinymceEditor"
    import {SimpleMultipleUserSelector as UserSelector} from '@/bizComponents/UserSelector'
    import dialogMixin from "@/mixins/dialogMixin"
    import {add, update, publish, withdraw} from "@/api/message/manage"
    import {isEmpty, mergeObj, resetObj} from '@/utils'
    import {elAlert, elConfirm, elSuccess} from "@/utils/message"
    import {auth} from "@/utils/auth"

    export default {
        name: "EditDialog",
        mixins: [dialogMixin],
        components: {DialogForm, DialogFormItem, TinymceEditor, UserSelector},
        props: {
            value: {type: Boolean, default: false},
            type: {type: String, default: 'see'},
            data: {
                type: Object,
                default: () => ({})
            },
            baseUrl: String
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
                    all: 1,
                    recipient: []
                },
                rules: {
                    title: [{required: true, message: '请输入标题', trigger: 'change'}],
                    type: [{required: true, message: '请选择消息类型', trigger: 'change'}],
                    all: [{required: true, message: '请选择通知对象', trigger: 'change'}]
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
                return this.type === 'add' && auth(this.baseUrl + '/add')
                    || this.form.status === 0 && this.type === 'edit' && auth(this.baseUrl + '/update')
            },

            canPublish() {
                //有发布权限、add模式或edit模式且status=0
                return auth(this.baseUrl + '/publish') && (this.type === 'add' || this.type === 'edit' && this.form.status === 0)
            },

            canWithdraw() {
                //有撤回权限、edit模式且status=1
                return auth(this.baseUrl + '/withdraw')
                    && this.type === 'edit'
                    && this.form.status === 1
            },
        },
        methods: {
            open() {
                if (this.type === 'add') return
                mergeObj(this.form, this.data)
                const recipient = this.data.recipient || ''
                this.form.recipient = recipient.split(',').map(i => parseInt(i))
            },

            clearForm() {
                resetObj(this.form)
                this.form.all = 1
                this.needSearch = false
                this.loading = false
                this.$nextTick(() => this.$refs.form.clearValidate())
            },

            cancel() {
                this.closeDialog()
                this.needSearch && this.$emit('search')

                setTimeout(() => this.clearForm(), 200)
                this.loading = false
            },

            save() {
                if (this.loading) return
                this.$refs.form.validate(v => {
                    if (!v) return
                    this.loading = true
                    const data = this.transformForm()
                    let promise = this.type === 'add' ? add(data) : update(data)
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
                        .then(() => publish(this.transformForm()))
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
                        return withdraw({id: this.form.id})
                    })
                    .then(({msg}) => {
                        elSuccess(msg)
                        this.needSearch = true
                        this.closeDialog()
                    })
                    .finally(() => this.loading = false)
            },

            transformForm() {
                if (this.form.all === 1) return this.form
                else return {...this.form, recipient: this.form.recipient.join(',')}
            }
        }
    }
</script>
