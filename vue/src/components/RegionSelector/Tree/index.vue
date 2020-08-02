<script type="text/jsx">
import Vue from 'vue'
import TreeDialog from "./TreeDialog"
import common from '../mixin'

const TreeDialogConstructor = Vue.extend(TreeDialog)

let limit, full

export default {
    name: "RegionTreeSelector",

    mixins: [common],

    data() {
        return {
            dialogVisible: false,
            limitTree: []
        }
    },

    methods: {
        openDialog() {
            if (this.limit) {
                this.initLimit()
                return limit.visible = true
            }
            else {
                !full && this.initFull()
                full.visible = true
            }
        },

        handler(payload) {
            this.$emit('select', ...payload)
        },

        initLimit() {
            if (limit) {
                limit.getChildrenOnSelect = this.getChildrenOnSelect
                limit.limit = this.limit
                limit.limitApi = this.limitApi
                return limit.init()
            }

            const data = {...this.$props, handler: this.handler}
            limit = new TreeDialogConstructor({data}).$mount()
            document.body.appendChild(limit.$el)

            return limit.init()
        },

        initFull() {
            if (full) {
                return full.getChildrenOnSelect = this.getChildrenOnSelect
            }

            const data = {getChildrenOnSelect: this.getChildrenOnSelect, handler: this.handler}
            full = new TreeDialogConstructor({data}).$mount()
            document.body.appendChild(full.$el)

            return full.init()
        }
    },

    render() {
        return (
            <el-input
                value={this.value}
                readonly={this.readonly}
                size={this.size}
                clearable
                v-on:clear={() => this.$emit('clear')}
                v-on:focus={this.openDialog}
            />
        )
    }
}
</script>
