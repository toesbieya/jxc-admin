<template>
    <div>
        <div class="tip-row">
            行政区划选择，<a href="https://github.com/modood/Administrative-divisions-of-China" target="_blank">数据由此获取</a>
        </div>
        <div class="tip-row">
            <p>树形</p>
            <div style="width: 300px">
                <region-selector :value="value" type="tree" @clear="clear" @select="select"/>
            </div>
        </div>
        <div class="tip-row">
            <p>选项卡型，支持v-model，<a href="https://github.com/TerryZ/v-region" target="_blank">样式搬的这个</a></p>
            <div style="width: 300px">
                <region-selector v-model="value" @clear="clear"/>
            </div>
        </div>
    </div>
</template>

<script>
import RegionSelector from '@/component/RegionSelector'

export default {
    name: "regionSelectorExample",
    components: {RegionSelector},
    data() {
        return {
            value: '北京市'
        }
    },
    methods: {
        clear() {
            this.value = null
        },
        select(obj) {
            this.value = obj.fullname
        }
    }
}
</script>
