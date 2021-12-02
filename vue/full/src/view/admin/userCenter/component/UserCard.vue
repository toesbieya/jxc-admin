<template>
    <el-card class="user-card" header="个人信息">
        <div class="avatar-container" title="点击上传头像" @click="uploadAvatarDialog=true">
            <el-avatar :src="avatar" icon="el-icon-user-solid"/>
        </div>

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
@import "~@/style/var";

.user-card {
    .avatar-container {
        position: relative;
        margin: 0 auto;
        width: 200px;
        box-shadow: rgb(204, 204, 204) 0 0 4px;
        border-radius: 50%;
        cursor: pointer;

        .el-avatar {
            display: block;
            width: 100%;
            height: 200px;

            i {
                line-height: 180px;
                font-size: 150px;
            }
        }
    }

    .user-info {
        padding-left: 0;
        list-style: none;

        li {
            border-bottom: 1px solid $--border-color-lighter;
            padding: 11px 0;
            font-size: 13px;
        }

        .user-right {
            float: right;

            a {
                color: $--color-primary;
            }
        }
    }
}
</style>
