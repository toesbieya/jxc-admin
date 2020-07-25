<template>
    <el-dialog
            v-drag-dialog
            v-bind="$props"
            v-on="$listeners"
            :visible="value"
            custom-class="form-dialog"
    >
        <slot slot="title" name="title">{{title}}</slot>

        <div class="form-dialog-container">
            <loading-mask :show="loading"/>
            <el-scrollbar>
                <div class="form-dialog-body">
                    <slot/>
                </div>
                <div style="margin-bottom: 17px"/>
            </el-scrollbar>
        </div>

        <slot slot="footer" name="footer"/>
    </el-dialog>
</template>

<script>
    import LoadingMask from '@/components/LoadingMask'

    export default {
        name: "FormDialog",

        components: {LoadingMask},

        props: {
            value: Boolean,
            appendToBody: Boolean,
            top: {type: String, default: '50px'},
            width: {type: String, default: '30%'},
            loading: {type: Boolean, default: true},
            title: String,
        }
    }
</script>

<style lang="scss">
    .form-dialog {
        .el-dialog__body {
            padding: 0;
        }

        .form-dialog-container {
            position: relative;
            width: 100%;

            .form-dialog-body {
                padding: 24px 24px 0
            }

            .el-form {
                padding: 0;
            }

            .el-upload.el-upload--picture-card {
                height: 100px;
                width: 100px;
                line-height: 105px;
            }

            .el-upload-list .el-upload-list__item {
                height: 100px;
                width: 100px;
            }
        }
    }
</style>
