<template>
    <tree-select :value="value" :data="data" :props="{label:'name'}" popper-append-to-body @input="input"/>
</template>

<script>
import TreeSelect from '@/component/TreeSelect'
import {get} from "@/api/system/department"
import {createTreeByWorker} from "@/util/tree"

export default {
    name: "DepartmentSelector",

    components: {TreeSelect},

    props: {value: Number},

    data() {
        return {
            data: []
        }
    },

    methods: {
        input(v, data) {
            this.$emit('input', v)
            this.$emit('get-name', data && data.fullname)
        },
    },

    mounted() {
        get
            .request(false)
            .then(({data}) => createTreeByWorker(data))
            .then(data => {
                this.data = data
            })
    }
}
</script>
