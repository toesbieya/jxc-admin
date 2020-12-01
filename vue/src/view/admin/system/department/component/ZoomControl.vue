<script type="text/jsx">
export default {
    name: "ZoomControl",

    functional: true,

    props: {
        value: {type: Number, default: 100},
        step: {type: Number, default: 20},
        min: {type: Number, default: 10},
        max: {type: Number, default: 200}
    },

    render(h, context) {
        const {value, step, max, min} = context.props
        const input = context.listeners.input

        function zoomIn() {
            const zoom = value + step
            if (zoom <= max) input(zoom)
        }

        function zoomOut() {
            const zoom = value - step
            if (zoom >= min) input(zoom)
        }

        return (
            <div class="zoom-wrapper">
                <el-button circle icon="el-icon-minus" size="small" title="缩小" type="info" on-click={zoomOut}/>
                <span class="zoom-number">{value}%</span>
                <el-button circle icon="el-icon-plus" size="small" title="放大" type="info" on-click={zoomIn}/>
            </div>
        )
    }
}
</script>

<style>
.zoom-wrapper .zoom-number {
    color: #657180;
    padding: 0 10px;
    display: inline-block;
    width: 60px;
    text-align: center;
}
</style>
