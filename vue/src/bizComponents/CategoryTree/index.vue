<template>
    <el-tree
            ref="tree"
            :data="data"
            :expand-on-click-node="false"
            :props="{label:'name'}"
            :filter-node-method="filterNodeMethod"
            highlight-current
            node-key="id"
            v-on="$listeners"
    />
</template>

<script>
    import {getAll} from "@/api/system/category"

    export default {
        name: "CategoryTree",

        props: {filterNodeMethod: Function},

        computed: {
            data() {
                return this.$store.state.dataCache.categoryTree
            }
        },

        mounted() {
            getAll()
                .then(data => this.$store.commit('dataCache/categories', data))
                .finally(() => this.loading = false)
        }
    }
</script>
