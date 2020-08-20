<template>
    <el-card class="user-card" header="个人信息">
        <el-avatar :size="200" :src="avatar" icon="el-icon-user-solid" @click.native="uploadAvatarDialog=true"/>

        <ul class="user-info">
            <li>
                用户昵称
                <div class="user-right">{{ name }}</div>
            </li>
            <li>
                所属部门
                <div class="user-right">{{ deptName }}</div>
            </li>
            <li>
                用户角色
                <div class="user-right">{{ roleName }}</div>
            </li>
            <li>
                安全设置
                <div class="user-right"><a @click="modifyPasswordDialog=true">修改密码</a></div>
            </li>
        </ul>

        <user-account v-model="modifyPasswordDialog"/>

        <upload-avatar v-model="uploadAvatarDialog"/>
    </el-card>
</template>

<script>
import {mapState} from 'vuex'
import UploadAvatar from './Avatar'
import UserAccount from './Account'

export default {
    name: 'UserCard',

    components: {UploadAvatar, UserAccount},

    data() {
        return {
            modifyPasswordDialog: false,
            uploadAvatarDialog: false
        }
    },

    computed: {
        ...mapState('user', ['name', 'avatar', 'roleName', 'deptName', 'admin'])
    }
}
</script>

<style lang="scss">
.user-card {
    .el-avatar {
        margin: 0 auto;
        display: block;
        cursor: pointer;

        i {
            line-height: 180px;
            font-size: 150px;
        }
    }

    .user-info {
        padding-left: 0;
        list-style: none;

        li {
            border-bottom: 1px solid #F0F3F4;
            padding: 11px 0;
            font-size: 13px;
        }

        .user-right {
            float: right;

            a {
                color: #317EF3;
            }
        }
    }
}
</style>
