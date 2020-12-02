/**
 * 在原基础上增加了 inlineIndent 属性，用于确定每级节点的单位缩进距离
 */
export default {
    inject: ['rootMenu'],

    props: {
        inlineIndent: {type: Number, default: 20}
    },

    computed: {
        indexPath() {
            const path = [this.index]
            let parent = this.$parent
            while (parent.$options.componentName !== 'ElMenu') {
                if (parent.index) {
                    path.unshift(parent.index)
                }
                parent = parent.$parent
            }
            return path
        },

        parentMenu() {
            let parent = this.$parent
            while (
                parent &&
                ['ElMenu', 'ElSubmenu'].indexOf(parent.$options.componentName) === -1
                ) {
                parent = parent.$parent
            }
            return parent
        },

        paddingStyle() {
            if (this.rootMenu.mode !== 'vertical') return {}

            let padding = this.inlineIndent

            if (!this.rootMenu.collapse) {
                let parent = this.$parent
                while (parent && parent.$options.componentName !== 'ElMenu') {
                    if (parent.$options.componentName === 'ElSubmenu') {
                        padding += this.inlineIndent
                    }
                    parent = parent.$parent
                }
            }

            return {'padding-left': `${padding}px`}
        }
    }
}
