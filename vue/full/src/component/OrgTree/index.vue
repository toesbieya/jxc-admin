<template>
    <div class="org-tree-container">
        <div class="org-tree" :class="{horizontal, collapsable}">
            <org-tree-node
                :data="dataCloned"
                :props="props"
                :horizontal="horizontal"
                :label-width="labelWidth"
                :collapsable="collapsable"
                :label-class-name="labelClassName"
                @on-expand="handleExpand"
                @on-node-click="handleNodeClick"
            />
        </div>
    </div>
</template>

<script>
import OrgTreeNode from "./node"

export default {
    name: 'OrgTree',

    components: {OrgTreeNode},

    props: {
        data: {required: true},
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
        labelWidth: [String, Number],
        labelClassName: [Function, String],
        expandAll: Boolean
    },

    data() {
        return {
            flatData: {},
            dataCloned: {}
        }
    },

    watch: {
        data(newData) {
            this._cloneData(newData)
            this._mapData(this.dataCloned, item => {
                const {expand} = this.flatData[item[this.props.id]] || {}
                if (expand) this.$set(item, this.props.expand, true)
            })
            this._toggleExpand(this.dataCloned, this.expandAll)
        },
        expandAll(status) {
            this._toggleExpand(this.dataCloned, status)
        }
    },

    methods: {
        _cloneData(newData) {
            this.dataCloned = JSON.parse(JSON.stringify(newData))
        },
        _setFlatData(data) {
            this.flatData[data[this.props.id]] = data
        },
        /**
         * 工具方法，用于遍历树状数据的每个节点， fn为在该节点做的操作，其有一个参数即当前节点数据
         */
        _mapData(data, fn) {
            fn(data)
            const children = data[this.props.children]
            children && children.forEach(child => this._mapData(child, fn))
        },
        /**
         * 用来便利所有节点数据，将树状数据扁平化存放到flatData，用于数据更新后展开状态的恢复
         */
        _updateExpandStatus() {
            this._mapData(this.dataCloned, this._setFlatData)
        },
        _toggleExpand(data, status) {
            if (Array.isArray(data)) {
                data.forEach(item => {
                    this.$set(item, this.props.expand, status)
                    const children = item[this.props.children]
                    if (children) {
                        this._toggleExpand(children, status)
                    }
                })
            }
            else {
                this.$set(data, this.props.expand, status)
                const children = data[this.props.children]
                if (children) {
                    this._toggleExpand(children, status)
                }
            }
        },
        collapse(list) {
            list.forEach(child => {
                if (child[this.props.expand]) {
                    child[this.props.expand] = false
                }
                const children = child[this.props.children]
                children && this.collapse(children)
            })
        },
        handleExpand(data) {
            if (this.props.expand in data) {
                data[this.props.expand] = !data[this.props.expand]
                const children = data[this.props.children]
                if (!data[this.props.expand] && children) {
                    this.collapse(children)
                }
            }
            else this.$set(data, this.props.expand, true)

            this.$emit('on-expand', data, data[this.props.expand])
            this._updateExpandStatus()
        },
        handleNodeClick(e, data) {
            this.$emit('on-node-click', e, data, () => this.handleExpand(data))
        }
    },

    mounted() {
        this._cloneData(this.data)
        this._updateExpandStatus()
        this._toggleExpand(this.dataCloned, this.expandAll)
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
