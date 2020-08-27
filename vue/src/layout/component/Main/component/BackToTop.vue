<script type="text/jsx">
export default {
    name: "BackToTop",

    functional: true,

    props: {render: Boolean},

    render(h, context) {
        const {props: {render}} = context
        if (!render) return
        return (
            <el-backtop target=".app-main .scroll-container" visibility-height={400} bottom={66}>
                <div class="back-to-top">
                    <v-icon icon="el-icon-top"/>
                </div>
            </el-backtop>
        )
    }
}
</script>
