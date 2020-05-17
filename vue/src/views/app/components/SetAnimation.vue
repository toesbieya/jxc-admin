<template>
    <el-dropdown size="medium" trigger="click" @command="$emit('select',$event)">
        <el-button type="text" title="选择背景动画">
            <svg-icon icon="show"/>
        </el-button>
        <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
                    v-for="p in animations"
                    :key="p.value"
                    :disabled="p.value===value"
                    :command="p.value"
            >
                {{p.name}}
            </el-dropdown-item>
        </el-dropdown-menu>
    </el-dropdown>
</template>

<script>
    export default {
        name: "SetAnimation",
        props: {
            value: String
        },
        data() {
            return {
                animations: [
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
                ]
            }
        },
        methods: {}
    }
</script>
