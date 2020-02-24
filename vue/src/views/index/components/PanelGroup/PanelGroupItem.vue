<template>
    <el-row
            class="card-panel"
            type="flex"
            justify="space-between"
            @mouseout.native="mouseout"
            @mouseover.native="mouseover"
    >
        <div class="card-panel-icon-wrapper" :style="style">
            <svg-icon :icon="icon" class-name="card-panel-icon"/>
        </div>
        <div class="card-panel-description">
            <div class="card-panel-text">{{text}}</div>
            <count-to class="card-panel-num" :duration="2000" :end-val="value" :start-val="0"/>
        </div>
    </el-row>
</template>

<script>
    import CountTo from 'vue-count-to'

    export default {
        name: "PanelGroupItem",
        components: {CountTo},
        props: {
            icon: String,
            color: String,
            text: String,
            value: Number
        },
        data() {
            return {
                style: 'color:' + this.color
            }
        },
        methods: {
            mouseout() {
                this.style = 'color:' + this.color
            },
            mouseover() {
                this.style = 'background:' + this.color
            }
        }
    }
</script>

<style lang="scss" src="./PanelGroupItem.scss"></style>
