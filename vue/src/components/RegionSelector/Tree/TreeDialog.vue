<script type="text/jsx">
    import {store,init} from '../store'
    import FormDialog from "@/components/FormDialog"
    import {createLimitTree, getNodeId} from "@/utils/tree"

    export default {
        components: {FormDialog},

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
                const promise = () => hasInit ? Promise.resolve() : init()
                return promise()
                    .then(() => this.limit ? this.limitApi() : Promise.resolve())
                    .then(data => data && (this.limitTree = createLimitTree(this.regionTree, data)))
                    .finally(() => this.loading = false)
            }
        },

        render() {
            return (
                <dialog-form
                    v-model={this.visible}
                    directives={[{name: 'drag-dialog'}]}
                    class="tree-dialog"
                    title="选择行政区域"
                    loading={this.loading}
                    appendToBody={true}
                    v-on:close={this.closeDialog}
                >
                    <el-tree
                        data={this.limit ? this.limitTree : this.regionTree}
                        expand-on-click-node={false}
                        node-key="id"
                        v-on:node-click={this.nodeClick}
                    >
                        {({data}) => (
                            <span className="el-tree-node__label">
                                {this.limit ? data.name + `(${data.value})` : data.name}
                            </span>
                        )}
                    </el-tree>
                </dialog-form>
            )
        }
    }
</script>
