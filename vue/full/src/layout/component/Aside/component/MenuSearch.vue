<template>
    <div class="aside-search">
        <el-input
            v-model="value"
            size="mini"
            clearable
            placeholder="搜索菜单"
            prefix-icon="el-icon-search"
            @input="search"
        />
    </div>
</template>

<script>
import {debounce} from "@/util"

export default {
    name: "MenuSearch",

    data: () => ({value: ''}),

    methods: {
        search(v) {
            this.$emit('search', v)
        }
    },

    created() {
        this.search = debounce(this.search)
    },

    beforeDestroy() {
        this.$emit('search', '')
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

//侧边栏折叠时不显示
.collapse .aside-search {
    display: none;
}

.aside-search {
    margin: 12px 8px;
}

//暗色主题
.dark .aside-search .el-input__inner {
    border: none;
    color: #ffffff;
    background-color: lighten($menu-background-dark, 5%);
}
</style>
