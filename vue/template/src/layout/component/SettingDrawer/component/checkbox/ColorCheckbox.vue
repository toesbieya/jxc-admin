<template>
    <div class="color-checkbox" :style="{'background-color': value}" @click="click">
        <i v-if="checked" class="el-icon-check"/>
    </div>
</template>

<script>
import mixin from './mixin'

export default {
    name: "ColorCheckBox",

    mixins: [mixin]
}
</script>
