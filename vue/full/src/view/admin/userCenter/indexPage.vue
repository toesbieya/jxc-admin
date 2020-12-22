<template>
    <el-row :gutter="20">
        <el-col :md="24" :lg="8" :xl="6" style="margin-bottom: 10px">
            <user-card/>
        </el-col>

        <el-col :md="24" :lg="16" :xl="18">
            <el-card>
                <el-tabs v-model="activeTab" stretch>
                    <el-tab-pane v-for="i in tabs" v-once :key="i.label" :name="i.name">
                        <template v-slot:label>
                            {{ i.label }}
                            <el-tooltip :content="i.intro" placement="top">
                                <i v-if="i.intro" class="el-icon-question" style="color: #4AB7BD"/>
                            </el-tooltip>
                        </template>
                    </el-tab-pane>

                    <transition mode="out-in" name="el-fade-in-linear">
                        <component :is="activeTab"/>
                    </transition>
                </el-tabs>
            </el-card>
        </el-col>
    </el-row>
</template>

<script>
import LoginHistory from "./component/LoginHistory"
import UserAction from "./component/UserAction"
import UserCard from "./component/UserCard"

export default {
    name: 'userCenter',

    components: {UserCard, LoginHistory, UserAction},

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
