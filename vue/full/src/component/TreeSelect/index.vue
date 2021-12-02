<template>
    <el-select
        ref="select"
        :value="value"
        :multiple="multiple"
        :disabled="disabled"
        :size="size"
        :clearable="clearable"
        :placeholder="placeholder"
        :popper-append-to-body="popperAppendToBody"
        :automatic-dropdown="automaticDropdown"
        @input="e => $emit('input',e)"
        @change="e => $emit('change',e)"
        @remove-tag="removeTag"
    >
        <template v-slot:empty>
            <div class="tree-select-container">
                <el-input
                    ref="search"
                    v-if="filterable && filterMethod"
                    v-model="search"
                    size="mini"
                    clearable
                    @input="onSearch"
                />
                <el-tree
                    ref="tree"
                    :data="data"
                    :node-key="nodeKey"
                    :props="props"
                    :show-checkbox="multiple"
                    :default-checked-keys="defaultCheckedKeys"
                    :current-node-key="multiple ? undefined : value"
                    :highlight-current="!multiple"
                    :expand-on-click-node="false"
                    :check-on-click-node="multiple"
                    :filter-node-method="filterMethod"
                    :accordion="accordion"
                    @node-click="nodeClick"
                    @check="check"
                >
                    <slot slot-scope="{node,data}">
                        <span :class="{'el-tree-node__label':true,'is-disabled':node.disabled}">{{ node.label }}</span>
                    </slot>
                </el-tree>
            </div>
        </template>
    </el-select>
</template>

<script>
import {debounce} from "@/util"
import {flatTree} from "@/util/tree"

export default {
    name: "TreeSelect",

    props: {
        value: {required: true},
        data: {type: Array, default: () => []},
        multiple: Boolean,
        disabled: Boolean,
        size: String,
        clearable: {type: Boolean, default: true},
        placeholder: String,
        filterable: Boolean,
        filterMethod: Function,
        nodeKey: {type: String, default: 'id'},
        props: {
            default() {
                return {
                    children: 'children',
                    label: 'label',
                    disabled: 'disabled'
                }
            }
        },
        accordion: {type: Boolean, default: true},
        popperAppendToBody: Boolean,
        automaticDropdown: Boolean
    },

    data() {
        return {
            currentNode: null,
            search: ''
        }
    },

    computed: {
        defaultCheckedKeys() {
            return this.multiple ? this.value : []
        }
    },

    watch: {
        value(v) {
            const method = this.multiple ? 'setCheckedKeys' : 'setCurrentKey'
            this.$refs.tree[method](v || null)
            if (!v && !this.multiple) this.currentNode = null
        },

        data() {
            this.initSelectOptions()
        }
    },

    methods: {
        removeTag(v) {
            const tree = this.$refs.tree
            const value = [...this.value]

            //被移除的选项的所有父级的ID
            const parents = []
            let removeNode = tree.getNode(v)
            while (removeNode && removeNode.parent) {
                parents.push(removeNode.parent.key)
                removeNode = removeNode.parent
            }

            //移除所有子级、所有父级
            for (let i = value.length - 1; i >= 0; i--) {
                const isChild = this.getNodeParent(tree.getNode(value[i]), ({key}) => key === v)
                if (isChild || parents.includes(value[i])) {
                    value.splice(i, 1)
                }
            }

            value.length < this.value.length && this.$emit('input', value)
        },

        onSearch(v) {
            this.$refs.tree.filter(v)
        },

        nodeClick(data, node) {
            if (this.multiple) return

            if (node.disabled) {
                return this.$refs.tree.setCurrentKey(null)
            }

            if (this.currentNode === data) {
                this.currentNode = null
                this.$refs.tree.setCurrentKey(null)
            }
            else this.currentNode = data

            this.$emit('input', this.currentNode && this.currentNode[this.nodeKey], this.currentNode)
            this.$refs.select.blur()
        },

        check(data, {checkedKeys}) {
            this.multiple && this.$emit('input', checkedKeys)
        },

        getNodeParent(node, predicate) {
            if (!node || predicate(node)) return node
            return this.getNodeParent(node.parent, predicate)
        },

        initSelectOptions() {
            this.$refs.select.cachedOptions =
                flatTree(this.data, this.props.children)
                    .map(i => ({
                        value: i[this.nodeKey],
                        currentLabel: i[this.props.label]
                    }))
            this.selectOptionsTrigger()
        },

        //模拟el-select对options的watch
        selectOptionsTrigger() {
            const select = this.$refs.select
            if (select.$isServer) return
            this.$nextTick(() => {
                select.broadcast('ElSelectDropdown', 'updatePopper')
            })
            if (select.multiple) {
                select.resetInputHeight()
            }
            let inputs = select.$el.querySelectorAll('input')
            if ([].indexOf.call(inputs, document.activeElement) === -1) {
                select.setSelected()
            }
            if (select.defaultFirstOption && (select.filterable || select.remote) && select.filteredOptionsCount) {
                select.checkDefaultFirstOption()
            }
        }
    },

    created() {
        this.onSearch = debounce(this.onSearch)
    },

    mounted() {
        this.currentNode = this.$refs.tree.getCurrentNode()
        this.initSelectOptions()
    }
}
</script>

<style lang="scss">
@import "~@/style/var";

.tree-select-container {
    padding: 8px;

    > .el-input {
        margin-bottom: 8px;
    }

    .el-tree-node__label.is-disabled {
        color: $--disabled-color-base;
        cursor: not-allowed;
    }
}
</style>
