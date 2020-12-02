<template>
    <div>
        <div class="tip-row">
            基于driver.js改造的用户导航
        </div>

        <div>
            <el-button plain @click="onClick">开始导航</el-button>
        </div>

        <div id="example-guide-step1" class="example-guide-step">我是步骤1</div>
        <div id="example-guide-step2" class="example-guide-step">我是步骤2</div>
        <div id="example-guide-step3" class="example-guide-step">我是步骤3</div>
        <div id="example-guide-step4" class="example-guide-step">我是步骤4</div>
        <div id="example-guide-step5" class="example-guide-step">我是步骤5</div>
    </div>
</template>

<script>
import {elSuccess, elConfirm} from "@/util/message"

export default {
    name: "guidePage",

    methods: {
        onClick() {
            this.$guide([
                {
                    element: '#example-guide-step1',
                    content: 'onNext钩子示例',
                    onNext: () => elConfirm('确认进行下一步吗？', false)
                },
                {
                    element: '#example-guide-step2',
                    content: 'onPrevious钩子展示',
                    onPrevious: () => elConfirm('确认回到上一步吗？', false)
                },
                {
                    element: '#example-guide-step3',
                    content: 'onHighlighted钩子示例',
                    onHighlighted: () => elSuccess('到达第三步！')
                },
                {
                    element: '#example-guide-step4',
                    content: `<h1>内容可以是html字符串</h1><img src="https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=4018557288,1217151095&fm=26&gp=0.jpg">`,
                },
                {
                    element: '#example-guide-step5',
                    content: '最后一步',
                },
            ])
        }
    }
}
</script>

<style scoped>
.example-guide-step {
    margin-top: 50px;
    height: 200px;
    width: 300px;
    background-color: blueviolet;
}
</style>
