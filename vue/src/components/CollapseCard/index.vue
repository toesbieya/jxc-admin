<template>
    <el-card :class="{collapsed}">
        <div slot="header" class="clearfix">
            <slot name="header">{{ header }}</slot>
            <i class="el-icon-arrow-up collapse-card-icon" @click="collapse"/>
        </div>
        <slot/>
    </el-card>
</template>

<script>
export default {
    name: "CollapseCard",

    props: {
        header: String
    },

    data() {
        return {
            collapsed: false
        }
    },

    methods: {
        collapse() {
            this.collapsed = !this.collapsed
        }
    }
}
</script>

<style>
.collapse-card-icon {
    float: right;
    font-weight: bold;
    transition: transform .3s;
    cursor: pointer;
}

.collapsed .collapse-card-icon {
    transform: rotate(180deg);
}

.el-card.collapsed > .el-card__body {
    display: none;
}
</style>
