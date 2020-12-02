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

    methods: {
        filter(v) {
            this.$refs.tree.filter(v)
        }
    },

    mounted() {
        if (this.data.length > 0) return
        getAll
            .request()
            .then(({data}) => this.$store.commit('dataCache/categories', data))
            .finally(() => this.loading = false)
    }
}
</script>
