<template>
    <div>
        <div class="tip-row">
            <a href="https://quasar.dev/vue-components/skeleton" target="_blank">骨架屏，详情请见quasar</a>
        </div>
        <div class="tip-row">
            <p>类型</p>
            <q-skeleton v-for="type in skeletonTypes" :key="type" :type="type"/>
        </div>
        <div class="tip-row">
            <p>动画</p>
            <q-skeleton v-for="animation in skeletonAnimations" key="animation" :animation="animation"/>
        </div>
        <div class="tip-row">
            <p>自定义颜色</p>
            <q-skeleton class-name="bg-primary" type="circle"/>
            <q-skeleton class-name="bg-info"/>
            <q-skeleton class-name="bg-warning" animation="pulse-y"/>
            <q-skeleton class-name="bg-error"/>
        </div>
    </div>
</template>

<script>
import QSkeleton from '@/component/Skeleton'
import {skeletonAnimations, skeletonTypes} from '@/component/Skeleton/constant'

export default {
    name: "skeletonExample",

    components: {QSkeleton},

    data() {
        return {
            skeletonTypes,
            skeletonAnimations
        }
    }
}
</script>

<style lang="scss" scoped>
@import "~@/style/var";

.q-skeleton + .q-skeleton {
    margin-top: 20px;
}

.bg-primary {
    background-color: $--color-primary;
}

.bg-info {
    background-color: $--color-info;
}

.bg-warning {
    background-color: $--color-warning;
}

.bg-error {
    background-color: $--color-danger;
}
</style>
