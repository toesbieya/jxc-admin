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
import TreeSelect from '@/component/TreeSelect'
import {getAll} from "@/api/system/category"
import {isEmpty} from "@/util"
import {elError} from "@/util/message"

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
            const arr = this.$store.state.dataCache.categoryTree
            this.addDisabledAttr(arr)
            return arr
        },
        selectableCategories() {
            return this.$store.state.dataCache.categories.filter(i => i.leaf)
        },
    },

    methods: {
        init() {
            if (this.tree.length === 0) {
                getAll
                    .request()
                    .then(({data}) => this.$store.commit('dataCache/categories', data))
            }
        },

        addDisabledAttr(arr) {
            arr.forEach(i => {
                i._disabled = !i.leaf
                i.children && this.addDisabledAttr(i.children)
            })
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
