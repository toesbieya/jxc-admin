<template>
    <el-card class="user-message-page">
        <el-tabs v-model="activeTab" :tab-position="tabPosition" stretch>
            <el-tab-pane v-for="{label,name} in tabs" :key="name" :name="name" :label="label"/>
            <div :class="tabContentClass">
                <message-stream :mode="activeTab"/>
            </div>
        </el-tabs>
    </el-card>
</template>

<script>
import {appGetters} from "el-admin-layout"
import MessageStream from './MessageStream'

export default {
    name: "userMessage",

    components: {MessageStream},

    data() {
        return {
            tabs: [{label: '未读消息', name: 'unread'}, {label: '已读消息', name: 'read'}],
            activeTab: 'unread',
        }
    },

    computed: {
        tabPosition() {
            return appGetters.isMobile ? 'top' : 'left'
        },
        tabContentClass() {
            return this.tabPosition === 'left' ? 'tab-main-right' : 'tab-main-top'
        }
    }
}
</script>

<style lang="scss">
.user-message-page {
    .el-tabs--left {
        .el-tabs__item.is-left {
            text-align: center;

            &.is-active {
                background-color: #f0faff;
            }
        }

        .el-tabs__header.is-left {
            min-width: 200px;
        }
    }

    .el-tabs__item {
        font-size: 16px;
    }

    .tab-main {
        &-right {
            padding: 8px 40px;
        }

        &-bottom {
            padding: 20px 40px;
        }
    }
}
</style>
