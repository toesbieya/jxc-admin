<template>
    <el-card v-show="show" v-loading="loading" :header="title" class="card-container" style="min-width: 520px;">
        <abstract-form :model="form" :rules="rules" class="edit-form">
            <el-form-item label="上级分类">
                <el-input :value="parentObj ? parentObj.name : ''" readonly/>
            </el-form-item>
            <el-form-item label="分类名称" prop="name">
                <el-input v-model="form.name" :readonly="type === 'see'" maxlength="50"/>
            </el-form-item>
            <el-form-item label="类 型" prop="leaf">
                <el-select v-model="form.leaf" :disabled="type === 'see'">
                    <el-option :value="false" label="分类"/>
                    <el-option :value="true" label="实体"/>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button plain size="small" @click="cancel">取 消</el-button>
                <el-button
                    v-show="['add','edit'].includes(type)"
                    size="small"
                    type="primary"
                    @click="confirm"
                >
                    确 定
                </el-button>
            </el-form-item>
        </abstract-form>
    </el-card>
</template>

<script>
import AbstractForm from "@/component/abstract/Form"
import {add, update} from "@/api/system/category"
import {isEmpty, mergeObj, waitUntilSuccess} from '@/util'
import {elConfirm} from "@/util/message"

export default {
    components: {AbstractForm},

    props: ['list'],

    data() {
        const typeValidate = (r, v, c) => {
            if (v && this.hasChildren) c('该分类下已有子节点')
            c()
        }
        return {
            loading: false,
            title: '',
            type: 'none',
            form: {
                id: null,
                pid: 0,
                name: '',
                leaf: false
            },
            parentObj: null,
            hasChildren: false,
            rules: {
                name: [{required: true, message: '请输入分类名称', trigger: 'change'}],
                leaf: [
                    {required: true, message: '请选择分类类型', trigger: 'change'},
                    {validator: typeValidate, trigger: 'change'}
                ]
            }
        }
    },

    computed: {
        show() {
            const ref = this.list()
            if (!ref) return false
            return this.type === 'add' || ['see', 'edit'].includes(this.type) && ref.currentCategory
        },

        confirmMessage() {
            switch (this.type) {
                case 'add':
                    return `确认添加新的分类【${this.form.name}】?`
                case 'edit':
                    return `确认提交对【${this.form.name}】作出的修改？`
                default:
                    return ''
            }
        }
    },

    methods: {
        add(parent) {
            this.title = '添加新分类'
            this.parentObj = parent
            this.type = 'add'
            this.form.pid = parent ? parent.id : 0
            this.form.name = ''
            this.form.leaf = false
            this.hasChildren = false
        },

        see(current, parent) {
            this.title = `分类信息(${current.name})`
            this.parentObj = parent
            this.type = 'see'
            mergeObj(this.form, current)
        },

        edit(current, parent) {
            this.title = `编辑分类(${current.name})`
            this.parentObj = parent
            this.type = 'edit'
            mergeObj(this.form, current)
            this.hasChildren = Array.isArray(current.children) && current.children.length > 0
        },

        confirm() {
            this.$refs.form.validate(v => {
                if (!v) return
                elConfirm(this.confirmMessage)
                    .then(() => this.type === 'add' ? add.request(this.form) : update.request(this.form))
                    .then(() => {
                        this.$emit('commit-success', this.type === 'add' ? '添加成功' : '修改成功')
                        this.cancel()
                    })
            })
        },

        cancel() {
            this.type = 'none'
            this.clearForm()
            this.$nextTick(() => this.$refs.form.clearValidate())
        },

        clearForm() {
            this.form.id = null
            this.form.pid = 0
            this.form.name = ''
            this.form.leaf = false
            this.parentObj = null
            this.hasChildren = false
        }
    },

    mounted() {
        waitUntilSuccess(
            () => !isEmpty(this.list()),
            () => ['add', 'see', 'edit'].forEach(event => this.list().$on(event, this[event]))
        )
    }
}
</script>

<style lang="scss" scoped>
.edit-form {
    width: 85%;
}
</style>
