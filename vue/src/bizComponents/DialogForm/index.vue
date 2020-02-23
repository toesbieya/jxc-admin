<template>
    <el-dialog
            v-drag-dialog
            class="dialog-form"
            :top="top"
            :visible="value"
            :width="width"
            @close="$emit('close')"
            @open="$emit('open')"
    >
        <slot slot="title" name="title">{{title}}</slot>
        <div class="dialog-form-container">
            <loading-mask :show="loading"/>
            <el-scrollbar>
                <div style="padding: 20px 20px 0">
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
        name: "DialogForm",
        components: {LoadingMask},
        props: {
            value: {type: Boolean, default: false},
            top: {type: String, default: '50px'},
            width: {type: String, default: '30%'},
            loading: {type: Boolean, default: true},
            title: {type: String, default: ''},
        }
    }
</script>

<style lang="scss">
    .dialog-form {
        .el-dialog__body {
            padding: 0;
        }

        .dialog-form-container {
            position: relative;
            width: 100%;

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
