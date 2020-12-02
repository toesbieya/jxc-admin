<script type="text/jsx">
import {store, init} from '../store'
import AbstractDialog from "@/component/abstract/Dialog"
import {createLimitTree, getNodeId} from "@/util/tree"

export default {
    components: {AbstractDialog},

    data() {
        return {
            loading: false,
            visible: false,
            handler: null,
            limitTree: [],
            getChildrenOnSelect: false,
            limit: false,
            limitApi: null
        }
    },

    computed: {
        regionTree() {
            return store.data
        }
    },

    methods: {
        closeDialog() {
            this.visible = false
        },

        nodeClick(obj) {
            const payload = [obj]

            if (this.getChildrenOnSelect) {
                const ids = getNodeId(obj.children)
                ids.unshift(obj.id)
                payload.push(ids)
            }

            this.handler(payload)

            this.closeDialog()
        },

        init() {
            this.loading = true
            const hasInit = this.regionTree.length > 0
            const promise = () => hasInit ? Promise.resolve() : init(this.regionDataUrl)
            return promise()
                .then(() => this.limit ? this.limitApi() : Promise.resolve())
                .then(data => data && (this.limitTree = createLimitTree(this.regionTree, data)))
                .finally(() => this.loading = false)
        }
    },

    render() {
        return (
            <abstract-dialog
                v-model={this.visible}
                class="tree-dialog"
                title="选择行政区域"
                loading={this.loading}
                v-on:close={this.closeDialog}
            >
                <el-tree
                    data={this.limit ? this.limitTree : this.regionTree}
                    expand-on-click-node={false}
                    node-key="id"
                    v-on:node-click={this.nodeClick}
                >
                    {({data}) => (
                        <span class="el-tree-node__label">
                            {this.limit ? data.name + `(${data.value})` : data.name}
                        </span>
                    )}
                </el-tree>
            </abstract-dialog>
        )
    }
}
</script>
