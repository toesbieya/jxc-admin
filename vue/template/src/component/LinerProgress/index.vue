<script type="text/jsx">
//从muse-ui处搬运，https://muse-ui.org/#/zh-CN/progress
export default {
    name: "LinerProgress",

    functional: true,

    props: {show: Boolean},

    render(h, context) {
        if (context.props.show) {
            return (
                <div class="liner-progress">
                    <div class="liner-progress-background"/>
                    <div class="linear-progress-indeterminate"/>
                </div>
            )
        }
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
