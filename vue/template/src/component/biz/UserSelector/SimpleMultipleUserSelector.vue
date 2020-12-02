<template>
    <el-select :value="value" :disabled="disabled" multiple collapse-tags size="small" @input="emit">
        <el-option
            v-for="user in data"
            :key="user.id"
            :label="user.nickName"
            :value="user.id"
        />
    </el-select>
</template>

<script>
import {search} from '@/api/system/user'

export default {
    name: "SimpleMultipleUserSelector",

    props: {value: Array, disabled: Boolean},

    data: () => ({data: []}),

    methods: {
        emit(v) {
            this.$emit('input', v)
        }
    },

    mounted() {
        search
            .request({page: 1, pageSize: 9999})
            .then(({data: {list}}) => this.data = list)
    }
}
</script>
