<template>
    <dialog-form :loading="loading" :title="title" :value="value" @close="cancel" @open="open">
        <el-form
                ref="form"
                :model="form"
                :rules="rules"
                label-position="right"
                label-width="75px"
                status-icon
        >
            <el-form-item label="名 称：" prop="name">
                <el-input v-model="form.name" maxlength="20"/>
            </el-form-item>
            <el-form-item label="状 态：" prop="status">
                <el-radio-group v-model="form.status">
                    <el-radio :label="1">启用</el-radio>
                    <el-radio :label="0">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="权 限：">
                <el-tree
                        ref="tree"
                        :data="resource"
                        :expand-on-click-node="false"
                        :props="{label:'name'}"
                        node-key="id"
                        show-checkbox
                />
            </el-form-item>
        </el-form>
        <template v-slot:footer>
            <el-dropdown placement="top" style="float: left" @command="treeCommand">
                <el-button plain size="small">
                    树形操作
                    <i class="el-icon-arrow-up el-icon--right"/>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="{action:'expand',level:0}">展开全部</el-dropdown-item>
                    <el-dropdown-item :command="{action:'collapse',level:0}">收起全部</el-dropdown-item>
                    <el-dropdown-item :command="{action:'expand',level:1}">仅展开一级</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <el-button plain size="small" @click="cancel">取 消</el-button>
            <el-button size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </dialog-form>
</template>

<script>
    import DialogForm from '@/bizComponents/DialogForm'
    import {addRole, updateRole} from "@/api/system/role"
    import {isEmpty, mergeObj} from '@/utils'
    import {elTreeControl} from '@/utils/tree'

    export default {
        name: "EditDialog",
        components: {DialogForm},
        props: {
            value: {type: Boolean, default: false},
            type: {type: String, default: 'add'},
            data: {type: Object, default: () => ({})},
        },
        data() {
            return {
                loading: false,
                form: {
                    id: null,
                    name: null,
                    status: 1,
                    resource_id: []
                },
                rules: {
                    name: [{required: true, message: '角色名称不能为空', trigger: 'change'}],
                    status: [{required: true, message: '角色状态不能为空', trigger: 'change'}],
                }
            }
        },
        computed: {
            title() {
                if (isEmpty(this.type)) return ''
                switch (this.type) {
                    case 'add':
                        return '添加角色'
                    case 'edit':
                        return '编辑角色'
                }
            },
            confirmMessage() {
                switch (this.type) {
                    case 'add':
                        return `确认添加新的角色【${this.form.name}】?`
                    case 'edit':
                        return `确认提交对【${this.data.name}】作出的修改？`
                    default:
                        return ''
                }
            },
            resource() {
                return this.$store.state.resource.tree
            }
        },
        methods: {
            treeCommand({action, level}) {
                elTreeControl(this.$refs.tree, action, level)
            },
            open() {
                this.$nextTick(() => {
                    if (this.type === 'edit') {
                        mergeObj(this.form, this.data)
                        this.$refs.tree.setCheckedKeys(this.form.resource_id)
                    }
                })
            },
            clearForm() {
                this.form.id = null
                this.form.name = null
                this.form.status = 1
                this.form.resource_id = []
                this.$refs.tree.setCheckedKeys([])
                this.$nextTick(() => this.$refs.form.clearValidate())
            },
            cancel() {
                this.$emit('input', false)
                this.clearForm()
            },
            confirm() {
                if (this.loading) return
                this.$refs.form.validate(v => {
                    if (!v) return
                    this.loading = true
                    let obj = {
                        ...this.form,
                        resource_id: this.$refs.tree.getCheckedKeys().join(',')
                    }
                    let promise = this.type === 'add' ? addRole(obj) : updateRole(obj)
                    promise
                        .then(({msg}) => this.$emit('success', msg))
                        .finally(() => this.loading = false)
                })
            },
        }
    }
</script>
