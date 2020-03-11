<template>
    <el-card class="user-card" header="用户信息">
        <div class="user-profile">
            <div class="box-center">
                <el-avatar :size="200" :src="avatar" icon="el-icon-user-solid"/>
                <div class="user-name text-center">{{name}}</div>
                <div class="user-role text-center text-muted">{{role_name}}</div>
            </div>
        </div>

        <!--<div class="user-bio">
            <div class="user-bio-section">
                <div class="user-bio-section-header">
                    <i class="el-icon-lock"/>
                    <span>修改锁屏密码</span>
                </div>
            </div>
        </div>-->
    </el-card>
</template>

<script>
    import {mapState} from 'vuex'

    export default {
        computed: {
            ...mapState('user', {
                name: state => state.name,
                avatar: state => state.avatar,
                role_name: state => state.role_name,
                admin: state => state.admin
            })
        }
    }
</script>

<style lang="scss">
    .user-card {
        .el-avatar i {
            line-height: 180px;
            font-size: 150px;
        }

        .text-muted {
            color: #777;
        }

        .user-profile {
            .user-name {
                font-weight: bold;
            }

            .box-center {
                margin: 0 auto;
                display: table;
                padding-top: 10px;
            }

            .user-role {
                padding-top: 10px;
                font-weight: 400;
                font-size: 14px;
            }
        }

        .user-bio {
            margin-top: 20px;
            color: #606266;

            span {
                padding-left: 4px;
            }

            .user-bio-section {
                font-size: 14px;
                padding: 15px 0;

                .user-bio-section-header {
                    border-bottom: 1px solid #dfe6ec;
                    padding-bottom: 10px;
                    margin-bottom: 10px;
                    font-weight: bold;
                }
            }
        }
    }
</style>
