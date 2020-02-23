<template>
    <el-row :gutter="20">
        <el-col :span="6">
            <user-card/>
        </el-col>
        <el-col :span="18">
            <el-card>
                <el-tabs v-model="activeTab">
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
    import UserCard from './components/UserCard'
    import UserAvatar from "@/views/userCenter/components/Avatar"
    import UserAccount from './components/Account'
    import LoginHistory from './components/LoginHistory'
    import UserAction from './components/UserAction'

    export default {
        name: 'userCenter',
        components: {UserCard, UserAvatar, UserAccount, LoginHistory, UserAction},
        data() {
            return {
                activeTab: 'user-avatar',
                tabs: [
                    {label: '上传头像', name: 'user-avatar'},
                    {label: '修改密码', name: 'user-account'},
                    {label: '登录历史', name: 'login-history', intro: '只保留最近7天的记录，想查询全部请联系管理员'},
                    {label: '操作记录', name: 'user-action', intro: '只保留最近7天的记录，想查询全部请联系管理员'},
                ]
            }
        }
    }
</script>
