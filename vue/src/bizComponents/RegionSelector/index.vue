<template>
    <el-dialog
            v-drag-dialog
            :visible="value"
            append-to-body
            title="选择行政区域"
            width="30%"
            top="50px"
            @close="closeDialog"
    >
        <el-scrollbar>
            <el-tree
                    ref="tree"
                    v-loading="loading"
                    :data="limit?limitTree:regionTree"
                    :expand-on-click-node="false"
                    node-key="id"
                    @node-click="nodeClick"
            >
                <span slot-scope="{data}" class="el-tree-node__label">
                    {{data.name}}
                    <template v-if="limit">({{data.value}})</template>
                </span>
            </el-tree>
        </el-scrollbar>
    </el-dialog>
</template>

<script>
    import {mapState} from 'vuex'
    import dialogMixin from "@/mixins/dialogMixin"
    import {createLimitTree, getNodeId} from "@/utils/tree"

    export default {
        name: "RegionSelector",
        mixins: [dialogMixin],
        props: {
            value: Boolean,
            limit: Boolean,
            limitApi: Function
        },
        data() {
            return {
                loading: false,
                limitTree: []
            }
        },
        computed: {
            ...mapState('dataCache', {
                regionTree: state => state.regionTree
            })
        },
        methods: {
            nodeClick(obj) {
                let ids = getNodeId(obj.children)
                ids.unshift(obj.id)
                this.$emit('select', obj, ids)
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
