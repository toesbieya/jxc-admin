<script type="text/jsx">
import CountTo from 'vue-count-to'

export default {
    name: "PanelGroupItem",

    functional: true,

    props: {
        icon: String,
        color: String,
        text: String,
        value: Number
    },

    render(h, context) {
        const {icon, color, text, value} = context.props
        return (
            <el-row class="card-panel" type="flex" justify="space-between" {...context.data}>
                <div class="card-panel-icon-wrapper" style={'color:' + color}>
                    <v-icon icon={icon} class="card-panel-icon"/>
                </div>
                <div class="card-panel-description">
                    <div class="card-panel-text">{text}</div>
                    <CountTo class="card-panel-num" end-val={value} start-val={0}/>
                </div>
            </el-row>
        )
    }
}
</script>

<style lang="scss">
@import "~@/style/var";
@import "~@/style/mixin.scss";

.card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background-color: #ffffff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
    border-color: rgba(0, 0, 0, .05);

    &:hover .card-panel-icon-wrapper {
        color: #ffffff;
    }

    .card-panel-icon-wrapper {
        margin: 14px 0 14px 14px;
        padding: 16px;
        transition: all 0.38s ease-out;
        border-radius: 6px;
    }

    .card-panel-icon {
        font-size: 48px;
    }

    .card-panel-description {
        font-weight: bold;
        margin: 26px 20px 0 26px;

        .card-panel-text {
            line-height: 18px;
            color: rgba(0, 0, 0, 0.45);
            font-size: 16px;
            margin-bottom: 12px;
        }

        .card-panel-num {
            font-size: 20px;
        }
    }
}

@include mobile {
    .card-panel-description {
        display: none;
    }

    .card-panel-icon-wrapper {
        float: none !important;
        width: 100%;
        height: 100%;
        margin: 0 !important;

        .icon {
            display: block;
            margin: 14px auto !important;
            float: none !important;
        }
    }
}

</style>
