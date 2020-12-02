<template>
    <div class="page-header">
        <span class="page-header-title">{{ title }}</span>

        <el-breadcrumb>
            <el-breadcrumb-item v-for="(item,index) in data" :key="item.path">
                <span :class="{'no-redirect': index !== data.length - 1}">{{ item.meta.title }}</span>
            </el-breadcrumb-item>
        </el-breadcrumb>
    </div>
</template>

<script>
export default {
    name: "PageHeader",

    data: () => ({data: [], title: ''}),

    watch: {
        $route: {
            immediate: true,
            handler(to) {
                const {path, meta: {title}, matched} = to
                if (!path.startsWith('/redirect')) {
                    this.title = title
                    this.data = matched.filter(item => item.meta.title)
                }
            }
        }
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: nowrap;
    font-size: 14px;
    line-height: $page-header-line-height;
    padding: $page-header-padding $page-view-margin $page-header-padding $page-view-margin;

    > .el-breadcrumb .no-redirect {
        color: #909399;
        cursor: text;
    }

    &-title {
        color: #303133;
        font-weight: 600;
        font-size: 18px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    // 页头渲染时页面顶部margin为0
    & + .page-view {
        margin-top: 0 !important;
    }
}
</style>
