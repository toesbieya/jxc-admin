/*
* 树形控件页通用混入
* */

const mixin = {
    data() {
        return {
            curNode: null
        }
    },

    watch: {
        curNode(v) {
            const ref = this.$refs.tree
            !v && ref && ref.setCurrentKey()
        }
    },

    methods: {
        nodeClick(data, node, ref) {
            if (this.curNode === node) {
                this.$refs.tree.setCurrentKey()
                this.curNode = null
            }
            else this.curNode = node

            if (this.afterNodeClick) {
                this.afterNodeClick(data, node, ref, !this.curNode)
            }
        }
    }
}

export default mixin
