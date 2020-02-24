<template>
    <div class="org-tree-container">
        <div :class="{horizontal, collapsable}" class="org-tree">
            <org-tree-node
                    v-bind="$listeners"
                    :button-render="buttonRender"
                    :collapsable="collapsable"
                    :data="dataCloned"
                    :horizontal="horizontal"
                    :label-class-name="labelClassName"
                    :label-width="labelWidth"
                    :node-render="nodeRender"
                    :props="props"
                    @on-expand="handleExpand"
                    @on-node-click="handleNodeClick"
            />
        </div>
    </div>
</template>

<script>
    import render from './node'
    import {deepClone as clonedeep} from '@/utils'

    export default {
        name: 'OrgTree',
        components: {
            OrgTreeNode: {
                render,
                functional: true
            }
        },
        props: {
            data: {
                type: Object,
                required: true
            },
            props: {
                type: Object,
                default: () => ({
                    id: 'id',
                    label: 'label',
                    expand: 'expand',
                    children: 'children'
                })
            },
            horizontal: Boolean,
            collapsable: Boolean,
            nodeRender: Function,
            buttonRender: Function,
            labelWidth: [String, Number],
            labelClassName: [Function, String],
            expandAll: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {
                flatData: {},
                dataCloned: {}
            }
        },
        watch: {
            data(newData) {
                this._handleData(newData)
                this._mapData(this.dataCloned, item => {
                    const {expand} = this.flatData[item[this.prop_id]] || {}
                    if (expand) this.$set(item, this.prop_expand, true)
                })
                this._toggleExpand(this.dataCloned, this.expandAll)
            },
            expandAll(status) {
                this._toggleExpand(this.dataCloned, status)
            }
        },
        computed: {
            prop_id() {
                return this.props.id
            },
            prop_label() {
                return this.props.label
            },
            prop_expand() {
                return this.props.expand
            },
            prop_children() {
                return this.props.children
            }
        },
        methods: {
            _handleData(data) {
                this._cloneData(data)
            },
            _cloneData(newData) {
                this.dataCloned = clonedeep(newData)
            },
            _setFlatData(data) {
                this.flatData[data[this.prop_id]] = data
            },
            /**
             * @description 工具方法，用于遍历树状数据的每个节点， fn为在该节点做的操作，其有一个参数即当前节点数据
             */
            _mapData(data, fn) {
                fn(data)
                const children = data[this.prop_children]
                if (children) {
                    children.forEach(child => {
                        this._mapData(child, fn)
                    })
                }
            },
            /**
             * @description 用来便利所有节点数据，将树状数据扁平化存放到flatData，用于数据更新后展开状态的恢复
             */
            _updateExpandStatus() {
                this._mapData(this.dataCloned, this._setFlatData)
            },
            collapse(list) {
                var _this = this
                list.forEach(child => {
                    if (child[this.prop_expand]) {
                        child[this.prop_expand] = false
                    }
                    const children = child[this.prop_children]
                    children && _this.collapse(children)
                })
            },
            handleExpand(data) {
                if (this.prop_expand in data) {
                    data[this.prop_expand] = !data[this.prop_expand]
                    const children = data[this.prop_children]
                    if (!data[this.prop_expand] && children) {
                        this.collapse(children)
                    }
                }
                else {
                    this.$set(data, this.prop_expand, true)
                }
                this.$emit('on-expand', data, data[this.prop_expand])
                this._updateExpandStatus()
            },
            _toggleExpand(data, status) {
                var _this = this
                if (Array.isArray(data)) {
                    data.forEach(item => {
                        _this.$set(item, this.prop_expand, status)
                        const children = item[this.prop_children]
                        if (children) {
                            _this._toggleExpand(children, status)
                        }
                    })
                }
                else {
                    _this.$set(data, this.prop_expand, status)
                    const children = data[this.prop_children]
                    if (children) {
                        _this._toggleExpand(children, status)
                    }
                }
            },
            handleNodeClick(e, data) {
                this.$emit('on-node-click', e, data, () => {
                    this.handleExpand(data)
                })
            },
            toggleExpand() {
                this._toggleExpand(this.dataCloned, this.expandAll)
                this._updateExpandStatus()
            }
        },
        mounted() {
            this._handleData(this.data)
            this._updateExpandStatus()
            this._toggleExpand(this.dataCloned, this.expandAll)
        }
    }
</script>

<style lang="scss" src="./tree.scss"></style>
