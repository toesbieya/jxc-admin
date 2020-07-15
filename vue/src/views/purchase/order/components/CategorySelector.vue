<template>
    <el-select :value="value" :disabled="disabled" size="small" popper-append-to-body @change="change" @input="emit">
        <el-option
                v-for="item in selectableCategories"
                :key="item.id"
                :label="item.name"
                :value="item.id"
        />
    </el-select>
</template>

<script>
    import {getAll} from "@/api/system/category"
    import {elError} from "@/utils/message"

    export default {
        name: "CategorySelector",
        props: {
            value: Number,
            disabled: Boolean,
            selected: {type: Array, default: () => []}
        },
        computed: {
            selectableCategories() {
                const cache = this.$store.state.dataCache
                if (!cache.categories) return []
                return cache.categories.filter(i => i.type === 1)
            },
        },
        methods: {
            init() {
                if (this.selectableCategories.length > 0) return
                getAll()
                    .then(data => this.$store.commit('dataCache/categories', data))
            },
            change(v) {
                if (!v || this.selected.length <= 0) return
                if (this.selected.includes(v)) {
                    elError('该分类已选择')
                    this.emit(null)
                }
            },
            emit(v) {
                this.$emit('input', v)
                if (v === 0 || v) {
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
