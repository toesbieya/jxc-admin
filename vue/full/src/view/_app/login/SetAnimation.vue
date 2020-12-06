<script type="text/jsx">
const animations = [
    {name: '无', value: ''},
    {name: '烟花', value: 'firework'},
    {name: '上帝之光', value: 'godrays'},
    {name: '简单粒子', value: 'moveFollowMouse'},
    {name: '球', value: 'particleBall'},
    {name: '网络', value: 'particleNetwork'},
    {name: '波浪', value: 'particleWave'},
    {name: '雨', value: 'reflectRain'},
    {name: '流星雨', value: 'sparkRain'},
    {name: '阳光', value: 'sunlight'},
    {name: '流光', value: 'fluxRay'},
]

export default {
    name: "SetAnimation",

    functional: true,

    props: {
        value: String,
        customClass: String
    },

    render(h, context) {
        const {value, customClass} = context.props
        return (
            <el-dropdown class={customClass} size="medium" trigger="click" onCommand={context.listeners.select}>
                <el-button type="text" title="选择背景动画">
                    <v-icon icon="svg-show"/>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    {
                        animations.map(animation => (
                            <el-dropdown-item
                                key={animation.value}
                                disabled={animation.value === value}
                                command={animation.value}
                            >
                                {animation.name}
                            </el-dropdown-item>
                        ))
                    }
                </el-dropdown-menu>
            </el-dropdown>
        )
    }
}
</script>
