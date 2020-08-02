<template functional>
    <div class="detail-page-header">
        <el-page-header @back="listeners.close" :content="props.title"/>

        <el-row>
            <el-row class="detail-page-header-left">
                <el-col v-for="i in props.description" :key="i.label" :xs="24" :sm="12">
                    <span class="detail-page-header-description-label">{{i.label}}</span>
                    <span class="detail-page-header-description-content">{{i.content}}</span>
                </el-col>
            </el-row>
            <div class="detail-page-header-extra">
                <div>
                    <div v-for="i in props.extra" :key="i.title">
                        <div class="detail-page-header-extra-title">{{i.title}}</div>
                        <div class="detail-page-header-extra-content">{{i.content}}</div>
                    </div>
                </div>
            </div>
        </el-row>
    </div>
</template>

<script>
    export default {
        name: "DocDetailHeader",

        props: {
            title: String,
            description: {
                type: Array,
                default: () => []
            },
            extra: {
                type: Array,
                default: () => []
            }
        }
    }
</script>
