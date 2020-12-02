<template>
    <div>
        <div class="tip-row">
            底部消息提示
        </div>

        <div>
            <el-button plain @click="$bottomTip('底部消息提示')">触发底部消息提示</el-button>
            <el-button plain @click="$bottomTip.close()">关闭底部消息提示</el-button>
        </div>
    </div>
</template>

<script>
export default {
    name: "bottomTipPage"
}
</script>
