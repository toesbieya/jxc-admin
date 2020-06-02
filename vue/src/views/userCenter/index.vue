<template>
    <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="8" :lg="6" :xl="5" style="margin-bottom: 10px">
            <user-card/>
        </el-col>
        <el-col :xs="24" :sm="24" :md="16" :lg="18" :xl="19">
            <el-card>
                <el-tabs v-model="activeTab" stretch>
                    <el-tab-pane v-for="i in tabs" v-once :key="i.label" :name="i.name">
                        <span slot="label">
                            {{i.label}}
                            <el-tooltip :content="i.intro" placement="top">
                                <i v-if="i.intro" class="el-icon-question" style="color: #4AB7BD"/>
                            </el-tooltip>
                        </span>
                    </el-tab-pane>
                    <transition mode="out-in" name="el-fade-in-linear">
                        <keep-alive exclude="user-account">
                            <component :is="activeTab"/>
                        </keep-alive>
                    </transition>
                </el-tabs>
            </el-card>
        </el-col>
    </el-row>
</template>

<script>
    import UserCard from "./components/UserCard"
    import LoginHistory from "./components/LoginHistory"

    export default {
        name: 'userCenter',
        components: {
            UserCard,
            LoginHistory,
            UserAction: () => import('./components/UserAction')
        },
        data() {
            return {
                activeTab: 'login-history',
                tabs: [
                    {label: '登录历史', name: 'login-history', intro: '只保留最近7天的记录'},
                    {label: '操作记录', name: 'user-action', intro: '只保留最近7天的记录'},
                ]
            }
        }
    }
</script>
