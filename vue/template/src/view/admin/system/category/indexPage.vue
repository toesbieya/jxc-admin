<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="8">
                <category-list ref="list" :form="() => $refs.form || null"/>
            </el-col>
            <el-col :span="16">
                <edit-form ref="form" :list="() => $refs.list || null"/>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import CategoryList from './component/List'
import EditForm from './component/Form'

export default {
    name: "categorySetting",

    components: {CategoryList, EditForm}
}
</script>
