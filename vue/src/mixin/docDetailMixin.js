import AbstractForm from '@/component/AbstractForm'
import AbstractFormItem from "@/component/AbstractForm/AbstractFormItem"
import AbstractTable from "@/component/AbstractTable"
import DocDetailHeader from "@/component/biz/doc/DocDetailHeader"
import DocDetailFooter from "@/component/biz/doc/DocDetailFooter"
import DocHistory from '@/component/biz/doc/DocHistory'
import DocSteps from '@/component/biz/doc/DocSteps'
import FormAnchor from "@/component/AbstractForm/FormAnchor"
import FormCard from '@/component/AbstractForm/FormCard'
import FormValidateInfo from "@/component/AbstractForm/FormValidateInfo"
import UploadFile from '@/component/UploadFile'
import {commonMethods} from "@/mixin/docTableMixin"
import {isEmpty, mergeObj} from '@/util'
import {elAlert, elConfirm, elPrompt, elSuccess} from "@/util/message"
import {deleteUpload} from "@/util/file"
import {auth} from "@/util/auth"
import {closeCurrentPage} from "@/util/route"

export default {
    components: {
        AbstractForm,
        AbstractFormItem,
        AbstractTable,
        DocDetailHeader,
        DocDetailFooter,
        DocHistory,
        DocSteps,
        FormAnchor,
        FormCard,
        FormValidateInfo,
        UploadFile
    },

    props: {
        //单据id
        id: String,
        //编辑模式,see,add,edit
        type: String
    },

    data() {
        return {
            attachmentSortSeed: 0,
            loading: true,
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
        title() {
            if (isEmpty(this.type)) return ''
            switch (this.type) {
                case 'see':
                    return `查看${this.docName}：${this.form.id}`
                case 'add':
                    return `添加${this.docName}`
                case 'edit':
                    return `编辑${this.docName}：${this.form.id}`
            }
        },
        user() {
            return this.$store.state.user
        },

        //权限判断以及根据状态控制是否可编辑
        canSave() {
            //add模式有添加权限、edit模式有编辑权限且status=0
            return this.type === 'add' && auth(`${this.baseUrl}/add`)
                || this.form.status === 0 && this.type === 'edit' && auth(`${this.baseUrl}/update`)
        },
        canCommit() {
            //有提交权限、add模式或edit模式且status=0
            return auth(`${this.baseUrl}/commit`)
                && (this.type === 'add' || this.type === 'edit' && this.form.status === 0)
        },
        canWithdraw() {
            //有撤回权限、当前用户是创建人、edit模式且status=1
            return auth(`${this.baseUrl}/withdraw`)
                && this.type === 'edit'
                && this.user.id === this.form.cid
                && this.form.status === 1
        },
        canPass() {
            //有通过权限、edit模式且status=1
            return auth(`${this.baseUrl}/pass`) && this.type === 'edit' && this.form.status === 1
        },
        canReject() {
            //有驳回权限、edit模式且status=1
            return auth(`${this.baseUrl}/reject`) && this.type === 'edit' && this.form.status === 1
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
                    const valid = this.validate()
                    if (!isEmpty(valid)) return elAlert(valid)
                }
                this.loading = true
                const promise = this.type === 'add' ? this.api.add(this.form) : this.api.update(this.form)
                promise
                    .then(({data, msg}) => {
                        this.needSearch()
                        elSuccess(msg)
                        this.afterSaveOrCommit(data)
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
                    const valid = this.validate()
                    if (!isEmpty(valid)) return elAlert(valid)
                }
                elConfirm('确认提交审核？')
                    .then(() => this.loading = true)
                    .then(() => this.api.commit(this.form))
                    .then(({data, msg}) => {
                        elSuccess(msg)
                        this.needSearch()
                        this.afterSaveOrCommit(data)
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
                    this.needSearch()
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
                    this.needSearch()
                    return this.close()
                })
                .catch(() => this.loading = false)
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
                    this.needSearch()
                    return this.close()
                })
                .catch(() => this.loading = false)
        },

        //初始化单据信息
        init(id) {
            this.loading = true
            if (isEmpty(id)) {
                return elAlert(`获取${this.docName}数据失败，请传入id`, this.close)
            }
            this.api.getById(id)
                .then(data => {
                    if (!data || id !== data.id) return Promise.reject()
                    this.modifyDataBeforeMerge && this.modifyDataBeforeMerge(data)
                    return Promise.resolve(mergeObj(this.form, data))
                })
                .then(() => this.afterInit ? this.afterInit() : Promise.resolve())
                .catch(e => {
                    console.error(e)
                    return elAlert(`获取${this.docName}数据失败，请重试`, this.close)
                })
                .finally(() => this.loading = false)
        },

        //保存、提交成功后需要判断后续动作
        afterSaveOrCommit(data) {
            if (this.type === 'add') {
                const editUrl = `${this.baseUrl}/detail/edit/${data}`
                return closeCurrentPage(editUrl)
            }
            else return this.init(this.form.id)
        },

        //关闭页面
        close() {
            //删除未保存的上传附件
            const deleteArr = []
            if (this.form.uploadImageList.length > 0) {
                deleteArr.push(...this.form.uploadImageList.map(i => i.url))
            }
            if (deleteArr.length > 0) {
                deleteUpload(deleteArr).catch(e => ({}))
            }

            return closeCurrentPage(this.baseUrl)
        },

        needSearch() {
            this.$store.commit('needSearch/emit', this.baseUrl)
        },

        //附件操作
        uploadSuccess(file, res) {
            this.form.uploadImageList.push({
                url: res.key,
                name: file.name,
                sort: this.attachmentSortSeed,
                size: file.size
            })
            this.attachmentSortSeed++
        },
        removeUpload(file) {
            this.form.deleteImageList.push(file.url)
            const index = this.form.uploadImageList.findIndex(i => i.url === file.url)
            if (index > -1) this.form.uploadImageList.splice(index, 1)
        }
    },

    mounted() {
        this.loading = false
        if (this.type !== 'add') return this.init(this.id)
    }
}
