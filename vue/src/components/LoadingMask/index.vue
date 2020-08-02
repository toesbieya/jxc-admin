<script type="text/jsx">
export default {
    name: "LoadingMask",

    functional: true,

    props: {show: Boolean},

    render(h, context) {
        if (context.props.show) {
            return (
                <div class="el-loading-mask">
                    <div class="el-loading-spinner">
                        <svg viewBox="25 25 50 50" class="circular">
                            <circle cx="50" cy="50" r="20" fill="none" class="path"/>
                        </svg>
                        <p>加载中...</p>
                    </div>
                </div>
            )
        }
    }
}
</script>
