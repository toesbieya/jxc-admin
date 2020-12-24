import {commonMethods} from "@/mixin/docTableMixin"
import AbstractForm from '@/component/abstract/Form'
import AbstractFormItem from "@/component/abstract/Form/item"
import AbstractTable from "@/component/abstract/Table"
import CollapseCard from '@/component/CollapseCard'
import DetailPage from "@/view/_common/DetailPage"
import DocHistory from '@/component/biz/doc/DocHistory'
import DocSteps from '@/component/biz/doc/DocSteps'
import QiniuUpload from '@/component/Upload/Qiniu'
import {isEmpty, mergeObj} from '@/util'
import {auth} from "@/util/auth"
import {deleteUploadBatch} from "@/api/file"
import {elAlert, elConfirm, elPrompt, elSuccess} from "@/util/message"
import {closeCurrentPage} from "@/util/route"

export default {
    components: {
        AbstractForm,
        AbstractFormItem,
        AbstractTable,
        CollapseCard,
        DetailPage,
        DocHistory,
        DocSteps,
        QiniuUpload
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
                uploadImageList: [],//新上传文件的key数组
                deleteImageList: [] //被删除的旧上传文件的key数组
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
            return this.type === 'add' && auth(this.api.add.url)
                || this.form.status === 0 && this.type === 'edit' && auth(this.api.update.url)
        },
        canCommit() {
            //有提交权限、add模式或edit模式且status=0
            return auth(this.api.commit.url)
                && (this.type === 'add' || this.type === 'edit' && this.form.status === 0)
        },
        canWithdraw() {
            //有撤回权限、当前用户是创建人、edit模式且status=1
            return auth(this.api.withdraw.url)
                && this.type === 'edit'
                && this.user.id === this.form.cid
                && this.form.status === 1
        },
        canPass() {
            //有通过权限、edit模式且status=1
            return auth(this.api.pass.url) && this.type === 'edit' && this.form.status === 1
        },
        canReject() {
            //有驳回权限、edit模式且status=1
            return auth(this.api.reject.url) && this.type === 'edit' && this.form.status === 1
        },

        //底部按钮
        buttons() {
            return [
                {_if: 'canSave', type: 'primary', content: '保 存', e: this.save},
                {_if: 'canCommit', type: 'primary', content: '提 交', e: this.commit},
                {_if: 'canWithdraw', type: 'danger', content: '撤 回', e: this.withdraw},
                {_if: 'canPass', type: 'success', content: '通 过', e: this.pass},
                {_if: 'canReject', type: 'danger', content: '驳 回', e: this.reject},
            ]
                .filter(i => this[i._if])
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
                const method = this.type === 'add' ? this.api.add : this.api.update
                method
                    .request(this.form)
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
                    .then(() => this.api.commit.request(this.form))
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
                    return this.api.withdraw.request({id: this.form.id, pid: this.form.pid})
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
                    return this.api.pass.request({id: this.form.id, pid: this.form.pid})
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
                    return this.api.reject.request({id: this.form.id, pid: this.form.pid, info})
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
            this.api.getById
                .request(id)
                .then(({data}) => {
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
        afterSaveOrCommit(id) {
            if (this.type === 'add') {
                const editUrl = this.$route.path.replace('/add', `/edit/${id}`)
                return closeCurrentPage(editUrl)
            }
            return this.init(this.form.id)
        },

        //关闭页面并跳转到列表页
        close() {
            return closeCurrentPage(this.getTablePageUrl())
        },
        //获取列表页的地址
        getTablePageUrl() {
            const url = this.$route.path
            const i = url.indexOf('/detail')
            const tablePageUrl = url.substring(0, i)
            return [...tablePageUrl].join('')
        },
        //列表页是否需要刷新数据
        needSearch() {
            this.$store.commit('needSearch/emit', this.getTablePageUrl())
        },

        /*附件操作*/
        //删除未保存的上传附件
        clearUnusedFile() {
            const deleteArr = this.form.uploadImageList.map(i => i.url)
            if (deleteArr.length > 0) {
                deleteUploadBatch.request(deleteArr).catch(e => ({}))
            }
        },
        uploadSuccess(file, res) {
            this.form.uploadImageList.push({
                url: res.key,
                name: file.name,
                sort: this.attachmentSortSeed,
                size: file.raw.size
            })
            this.attachmentSortSeed++
        },
        removeUpload({key, isNew}) {
            //被删除的文件不是本次上传的
            if (!isNew) {
                return this.form.deleteImageList.push(key)
            }

            const index = this.form.uploadImageList.findIndex(i => i.url === key)
            index > -1 && this.form.uploadImageList.splice(index, 1)
        }
    },

    mounted() {
        this.loading = false
        if (this.type !== 'add') return this.init(this.id)
    },

    beforeDestroy() {
        this.clearUnusedFile()
    }
}
