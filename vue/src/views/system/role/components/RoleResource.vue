<script type="text/jsx">
    import {createTree} from "@/utils/tree"

    export default {
        name: "RoleResource",

        functional: true,

        props: {
            ids: {type: Array, default: () => []},
            map: {type: Object, default: () => ({})}
        },

        render(h, context) {
            const {ids, map} = context.props
            let tree = []

            for (let id of ids) map[id] && tree.push(map[id])

            tree = createTree(tree)

            const nodes = []

            const fun = (arr, left) => {
                for (let r of arr) {
                    nodes.push(
                        <span style={'margin-left:' + left + 'px'}>
                            <svg-icon icon="check"/>
                            {r.name}
                        </span>
                    )
                    if (r.children.length > 0) {
                        if (r.children[0].children.length > 0) nodes.push(<p/>)
                        fun(r.children, left + 20)
                    }
                }
                nodes.push(<p/>)
            }

            fun(tree, 0)

            return nodes
        }
    }
</script>

<style lang="scss" scoped>
    span {
        .svg-icon {
            color: $--color-primary
        }
    }

    span + span {
        margin-left: 10px;
    }
</style>
