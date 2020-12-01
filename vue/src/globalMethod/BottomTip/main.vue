<template>
    <transition name="el-zoom-in-bottom" @after-leave="handleAfterLeave">
        <div v-show="value" class="bottom-tip el-alert--error is-dark">
            <i class="el-icon-warning tip-icon"/>
            <span class="tip-content">{{ message }}</span>
        </div>
    </transition>
</template>

<script>
export default {
    name: "BottomTip",

    data() {
        return {
            value: false,
            message: null
        }
    },

    methods: {
        handleAfterLeave() {
            this.$destroy(true)
            this.$el.parentNode.removeChild(this.$el)
        },
        close() {
            this.value = false
        },
    }
}
</script>

<style lang="scss">
.bottom-tip {
    position: absolute;
    width: 100%;
    bottom: 0;
    z-index: 100;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 20px;

    .tip-content {
        font-weight: bold;
        margin: 10px;
    }
}
</style>
