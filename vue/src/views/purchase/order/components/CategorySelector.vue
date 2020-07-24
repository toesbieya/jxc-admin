<template>
    <tree-select
            :value="value"
            :data="tree"
            :disabled="disabled"
            :props="props"
            size="small"
            popper-append-to-body
            @change="change"
            @input="emit"
    />
</template>

<script>
    import TreeSelect from '@/components/TreeSelect'
    import {getAll} from "@/api/system/category"
    import {isEmpty} from "@/utils"
    import {elError} from "@/utils/message"

    export default {
        name: "CategorySelector",

        components: {TreeSelect},

        props: {
            value: {required: true},
            disabled: Boolean,
            selected: {type: Array, default: () => []}
        },

        data() {
            return {
                props: {
                    label: 'name',
                    children: 'children',
                    disabled: '_disabled'
                }
            }
        },

        computed: {
            tree() {
                return this.$store.state.dataCache.categoryTree
            },
            selectableCategories() {
                return this.$store.state.dataCache.categories.filter(i => i.type === 1)
            },
        },

        methods: {
            init() {
                if (this.tree.length === 0) {
                    getAll()
                        .then(data => {
                            data.forEach(i => i._disabled = i.type === 0)
                            this.$store.commit('dataCache/categories', data)
                        })
                }
            },

            change(v) {
                if (isEmpty(v) || this.selected.length <= 0) return
                if (this.selected.includes(v)) {
                    elError('该分类已选择')
                    this.emit(null)
                }
            },

            emit(v) {
                this.$emit('input', v)
                if (!isEmpty(v)) {
                    let item = this.selectableCategories.find(i => i.id === v)
                    this.$emit('get-name', item ? item.name : null)
                }
                else this.$emit('get-name', null)
            }
        },

        mounted() {
            this.init()
        }
    }
</script>
