<template>
    <div class="img-checkbox" :title="label" @click="click">
        <img :src="img"/>
        <i v-if="checked" class="el-icon-check"/>
    </div>
</template>

<script>
import mixin from './mixin'

export default {
    name: "ImgCheckBox",

    mixins: [mixin],

    props: {
        img: String,
        label: String
    }
}
</script>
