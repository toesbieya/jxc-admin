<template>
    <el-breadcrumb class="app-breadcrumb">
        <transition-group name="breadcrumb">
            <el-breadcrumb-item v-for="(item,index) in data" :key="item.path">
                <span :class="{'no-redirect': index !== data.length-1}">{{ item.meta.title }}</span>
            </el-breadcrumb-item>
        </transition-group>
    </el-breadcrumb>
</template>

<script>
export default {
    data: () => ({data: []}),

    watch: {
        $route(route) {
            if (route.path.startsWith('/redirect')) return
            this.getBreadcrumb()
        }
    },

    methods: {
        getBreadcrumb() {
            // only show routes with meta.title
            this.data = this.$route.matched.filter(item => item.meta.title)
        }
    },

    mounted() {
        this.getBreadcrumb()
    }
}
</script>

<style lang="scss" scoped>
.app-breadcrumb {
    width: 100%;
    background: #fff;
    padding: 16px 24px;

    .no-redirect {
        color: #909399;
        cursor: text;
    }
}
</style>
