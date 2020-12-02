<template>
    <div>
        <div class="tip-row">
            可拖拽的dialog
        </div>

        <el-button @click="value = true">打开dialog</el-button>

        <el-dialog v-drag-dialog :visible.sync="value" title="我可以拖动">
            <div>蛤蛤</div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "dragDialogPage",

    data() {
        return {
            value: false
        }
    }
}
</script>
