<template>
    <div>
        <el-row class="tip-row">
            <a href="https://github.com/Kevin-269581661/vue-puzzle-verification" target="_blank">网上搬的例子</a>
        </el-row>
        <puzzle-verification :puzzleImgList="puzzleImgList"/>
    </div>
</template>
<script>
    import PuzzleVerification from '@/components/PuzzleVerify'

    export default {
        components: {PuzzleVerification},
        data() {
            return {
                puzzleImgList: ['http://q4r31dqam.bkt.clouddn.com/image/56.jpg']
            }
        }
    }
</script>
