<template>
    <div>
        <el-input :value="value" :readonly="readonly" clearable @clear="$emit('clear')" @focus="dialogVisible=true"/>
        <dialog-form
                v-drag-dialog
                v-model="dialogVisible"
                :loading="loading"
                append-to-body
                title="选择行政区域"
                @close="closeDialog"
        >
            <el-tree
                    ref="tree"
                    :data="limit ? limitTree : regionTree"
                    :expand-on-click-node="false"
                    node-key="id"
                    @node-click="nodeClick"
            >
                <span slot-scope="{data}" class="el-tree-node__label">
                    {{limit ? data.name + `(${data.value})` : data.name}}
                </span>
            </el-tree>
        </dialog-form>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import DialogForm from '@/components/DialogForm'
    import {createLimitTree, getNodeId} from "@/utils/tree"

    export default {
        name: "RegionTreeSelector",

        components: {DialogForm},

        props: {
            value: String,
            readonly: Boolean,
            getChildrenOnClick: Boolean,
            limit: Boolean,
            limitApi: Function
        },

        data() {
            return {
                loading: false,
                dialogVisible: false,
                limitTree: []
            }
        },

        computed: {
            ...mapState('dataCache', {
                regionTree: state => state.regionTree
            })
        },

        methods: {
            closeDialog() {
                this.dialogVisible = false
            },

            nodeClick(obj) {
                const payload = [obj]
                if (this.getChildrenOnClick) {
                    let ids = getNodeId(obj.children)
                    ids.unshift(obj.id)
                    payload.push(ids)
                }

                this.$emit('select', ...payload)

                this.closeDialog()
            },

            init() {
                this.loading = true
                const hasInit = this.regionTree.length > 0
                const promise = () => hasInit ? Promise.resolve() : this.$store.dispatch('dataCache/initRegion')
                promise()
                    .then(() => this.limit ? this.limitApi() : Promise.resolve())
                    .then(data => data && (this.limitTree = createLimitTree(this.regionTree, data)))
                    .finally(() => this.loading = false)
            }
        },

        mounted() {
            this.init()
        }
    }
</script>
