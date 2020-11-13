<script type="text/jsx">
export default {
    functional: true,

    render() {
        return (
            <el-backtop target=".page-main .scroll-container" visibility-height={400} bottom={66}>
                <div class="back-to-top">
                    <v-icon icon="el-icon-top"/>
                </div>
            </el-backtop>
        )
    }
}
</script>
