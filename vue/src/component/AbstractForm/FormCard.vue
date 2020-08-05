<template>
    <el-card :class="{collapsed}">
        <div slot="header" class="clearfix">
            <slot name="header">{{ header }}</slot>
            <i class="el-icon-arrow-up form-card-icon"/>
        </div>
        <slot/>
    </el-card>
</template>

<script>
export default {
    name: "FormCard",

    props: {
        header: String
    },

    data: () => ({collapsed: false}),

    methods: {
        collapse() {
            this.collapsed = !this.collapsed
        }
    },

    mounted() {
        const dom = this.$el.querySelector('.el-card__header')
        dom.addEventListener('click', this.collapse)
        this.$once('hook:beforeDestroy', () => {
            dom.removeEventListener('click', this.collapse)
        })
    }
}
</script>

<style>
.form-card-icon {
    float: right;
    font-weight: bold;
    transition: transform .3s;
    cursor: pointer;
}

.collapsed .form-card-icon {
    transform: rotate(180deg);
}

.el-card.collapsed > .el-card__body {
    display: none;
}
</style>
