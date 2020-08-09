<template>
    <div style="width: 300px">
        <div class="tip-row">单选</div>
        <tree-select v-model="single" :data="data" filterable :filter-method="filter" @input="singleInput"/>

        <div class="tip-row">多选</div>
        <tree-select v-model="multiple" :data="data" multiple filterable :filter-method="filter"/>
    </div>
</template>

<script>
import TreeSelect from '@/component/TreeSelect'

export default {
    name: "treeSelectExample",

    components: {TreeSelect},

    data() {
        return {
            single: '',
            multiple: [],
            data: [
                {id: 1, label: '一级 1', children: [{id: 2, label: '二级 1-1'}, {id: 3, label: '二级 1-2'}]},
                {id: 4, label: '一级 2', children: [{id: 5, label: '二级 2-1'}]},
            ],
        }
    },

    methods: {
        filter(v, data) {
            return data.label.includes(v)
        },

        singleInput(v, data) {
            console.log(data)
        }
    }
}
</script>
