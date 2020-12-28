<template>
    <el-card :class="{collapsed}">
        <template v-slot:header>
            <div class="clearfix">
                <slot name="header">{{ header }}</slot>
                <i class="el-icon-arrow-up collapse-card-icon"/>
            </div>
        </template>

        <slot/>
    </el-card>
</template>

<script>
export default {
    name: "CollapseCard",

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
