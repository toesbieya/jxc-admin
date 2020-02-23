import UploadFile from '@/bizComponents/UploadFile'
import DialogForm from '@/bizComponents/DialogForm'
import DialogFormItem from "@/bizComponents/DialogForm/DialogFormItem"
import DocumentSteps from '@/bizComponents/DocumentSteps'
import DocumentHistory from '@/bizComponents/DocumentHistory'
import {commonMethods} from "@/mixins/bizDocumentTableMixin"
import {isEmpty, mergeObj, resetObj, timeFormat} from '@/utils'
import {elAlert, elConfirm, elPrompt, elSuccess} from "@/utils/message"
import {getDocumentHistoryByPid} from "@/api/documentHistory"
import {deleteUpload} from "@/utils/file"
import {auth} from "@/utils/auth"

export default {
    components: {DialogForm, DialogFormItem, DocumentSteps, DocumentHistory, UploadFile},
    props: {
        //dialog显隐
        value: {type: Boolean, default: false},
        //编辑模式,see,add,edit
        type: {type: String, default: 'see'},
        //单据id
        id: String,
        baseUrl: String,
    },
    data() {
        return {
            //dialog loading标志
            loading: true,
            //关闭dialog后是否需要更新列表
            needSearch: false,
            //单据提交历史
            history: [],
            form: {
                id: null,
                cid: null,
                cname: null,
                ctime: null,
                vid: null,
                vname: null,
                vtime: null,
                status: 0,
                remark: null,
                data: [],
                imageList: [],
                uploadImageList: [],
                deleteImageList: []
            },
            rules: {}
        }
    },
    computed: {
        //dialog标题
        title() {
            if (isEmpty(this.type)) return ''
            switch (this.type) {
                case 'see':
                    return this.documentName + '信息'
                case 'add':
                    return '添加' + this.documentName
                case 'edit':
                    return '编辑' + this.documentName
            }
        },
        user() {
            return this.$store.state.user
        },

        /*
        * 权限判断以及根据状态控制是否可编辑
        * */
        canSave() {
            //add模式有添加权限、edit模式有编辑权限且status=0
            return this.type === 'add' && auth(this.baseUrl + '/add')
                || this.form.status === 0 && this.type === 'edit' && auth(this.baseUrl + '/update')
        },
        canCommit() {
            //有提交权限、add模式或edit模式且status=0
            return auth(this.baseUrl + '/commit') && (this.type === 'add' || this.type === 'edit' && this.form.status === 0)
        },
        canWithdraw() {
            //有撤回权限、当前用户是创建人、edit模式且status=1
            return auth(this.baseUrl + '/withdraw')
                && this.type === 'edit'
                && this.user.id === this.form.cid
                && this.form.status === 1
        },
        canPass() {
            //有通过权限、edit模式且status=1
            return auth(this.baseUrl + '/pass') && this.type === 'edit' && this.form.status === 1
        },
        canReject() {
            //有驳回权限、edit模式且status=1
            return auth(this.baseUrl + '/reject') && this.type === 'edit' && this.form.status === 1
        }
    },
    methods: {
        ...commonMethods,

        //保存，分为添加或修改
        save() {
            if (this.loading) return
            this.$refs.form.validate(v => {
                if (!v) return
                if (this.validate) {
                    let valid = this.validate()
                    if (!isEmpty(valid)) return elAlert(valid)
                }
                this.loading = true
                let promise = this.type === 'add' ? this.api.add(this.form) : this.api.update(this.form)
                promise
                    .then(({data, msg}) => {
                        this.needSearch = true
                        elSuccess(msg)
                        this.$emit('success', this.type)
                        const id = this.type === 'add' ? data : this.form.id
                        this.$emit('update:type', 'edit')
                        this.init(id)
                    })
                    .catch(() => this.loading = false)
            })
        },

        //提交，状态由拟定->待审核
        commit() {
            if (this.loading) return
            this.$refs.form.validate(v => {
                if (!v) return
                if (this.validate) {
                    let valid = this.validate()
                    if (!isEmpty(valid)) return elAlert(valid)
                }
                elConfirm('确认提交审核？')
                    .then(() => this.loading = true)
                    .then(() => this.api.commit(this.form))
                    .then(({data, msg}) => {
                        elSuccess(msg)
                        this.needSearch = true
                        this.$emit('success', 'commit')
                        const id = this.type === 'add' ? data : this.form.id
                        this.$emit('update:type', 'edit')
                        this.init(id)
                    })
                    .catch(() => this.loading = false)
            })
        },

        //撤回自己的单据，状态由待审核->拟定
        withdraw() {
            if (this.loading) return
            elConfirm('确认撤回？')
                .then(() => {
                    this.loading = true
                    return this.api.withdraw({id: this.form.id, pid: this.form.pid})
                })
                .then(({msg}) => {
                    elSuccess(msg)
                    this.needSearch = true
                    this.$emit('success', 'withdraw')
                    this.init(this.form.id)
                })
                .catch(() => this.loading = false)
        },

        //通过单据，状态由待审核->已审核
        pass() {
            if (this.loading) return
            elConfirm('确认通过审核？')
                .then(() => {
                    this.loading = true
                    return this.api.pass({id: this.form.id, pid: this.form.pid})
                })
                .then(({msg}) => {
                    elSuccess(msg)
                    this.$emit('success', 'pass')
                    this.needSearch = true
                    this.cancel()
                })
                .finally(() => this.loading = false)
        },

        //驳回单据，状态由待审核->拟定
        reject() {
            if (this.loading) return
            elPrompt('请输入驳回理由')
                .then(info => {
                    this.loading = true
                    return this.api.reject({id: this.form.id, pid: this.form.pid, info})
                })
                .then(({msg}) => {
                    elSuccess(msg)
                    this.$emit('success', 'reject')
                    this.needSearch = true
                    this.cancel()
                })
                .finally(() => this.loading = false)
        },

        //获取单据的提交历史
        getHistory() {
            if (isEmpty(this.id) || this.history.length > 0) return
            getDocumentHistoryByPid(this.id)
                .then(history => {
                    history.forEach(i => {
                        i.time = timeFormat(null, new Date(i.time))
                        let type = null
                        switch (i.type) {
                            case 0:
                                type = '撤回'
                                break
                            case 1:
                                type = '提交'
                                break
                            case 2:
                                type = '通过'
                                break
                            case 3:
                                type = '驳回'
                                break
                        }
                        i.type = type
                    })
                    this.history = history
                })
        },

        //初始化单据信息
        init(id) {
            this.loading = true
            if (isEmpty(id)) {
                return elAlert(`获取${this.documentName}数据失败，请传入id`, () => this.cancel())
            }
            this.api.getById(id)
                .then(data => {
                    if (!data || id !== data.id) return Promise.reject()
                    return Promise.resolve(mergeObj(this.form, data))
                })
                .then(() => this.afterInit ? this.afterInit() : Promise.resolve())
                .catch(e => {
                    console.log(e)
                    return elAlert(`获取${this.documentName}数据失败，请重试`, () => this.cancel())
                })
                .finally(() => this.loading = false)
        },

        //dialog打开时
        open() {
            this.needSearch = false
            this.loading = false
            if (this.type !== 'add') return this.init(this.id)
        },

        //关闭dialog并清除单据数据
        cancel() {
            this.$emit('input', false)
            if (this.needSearch) this.$emit('search')
            //删除未保存的上传附件
            let deleteArr = []
            if (this.form.uploadImageList.length > 0) {
                deleteArr.push(...this.form.uploadImageList.map(i => i.url))
            }
            if (deleteArr.length > 0) {
                deleteUpload(deleteArr).catch(e => ({}))
            }
            setTimeout(() => this.clearForm(), 500)
            this.loading = false
        },

        //清除单据数据
        clearForm() {
            resetObj(this.form)
            this.history = []
            this.parentSubList && (this.parentSubList = [])
            this.$nextTick(() => this.$refs.form.resetFields())
        },

        //附件操作
        uploadSuccess(file, res) {
            this.form.uploadImageList.push({url: res.key, name: file.name, order: 0, size: file.size})
        },
        removeUpload(file) {
            this.form.deleteImageList.push(file.url)
            let index = this.form.uploadImageList.findIndex(i => i.url === file.url)
            if (index > -1) this.form.uploadImageList.splice(index, 1)
        }
    }
}
