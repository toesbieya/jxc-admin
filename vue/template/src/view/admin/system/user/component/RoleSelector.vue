<template>
    <el-select :value="value" clearable @clear="() => emit(null)" @input="emit">
        <el-option
            v-for="item in roles"
            :key="item.id"
            :label="item.name"
            :value="item.id"
        />
    </el-select>
</template>

<script>
import {get} from "@/api/system/role"

export default {
    name: "RoleSelector",

    props: {value: Number},

    data: () => ({roles: []}),

    methods: {
        init() {
            get.request().then(({data}) => this.roles = data)
        },

        emit(v) {
            this.$emit('input', v)
            if (v === 0 || v) {
                let item = this.roles.find(i => i.id === v)
                this.$emit('get-name', item ? item.name : null)
            }
            else {
                this.$emit('get-name', null)
            }
        }
    },

    mounted() {
        this.init()
    }
}
</script>
