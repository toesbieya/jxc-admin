<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="8">
                <category-list :form="()=>$refs.form||null" ref="list"/>
            </el-col>
            <el-col :span="16">
                <edit-form :list="()=>$refs.list||null" ref="form"/>
            </el-col>
        </el-row>
    </div>
</template>

<script>
    import CategoryList from './components/List'
    import EditForm from './components/Form'

    export default {
        name: "categorySetting",

        components: {CategoryList, EditForm}
    }
</script>
