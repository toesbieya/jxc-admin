/*
* 数据缓存
* 比如商品分类信息等等
* */
import {createTree} from '@/util/tree'

const state = {
    categories: [],
    categoryTree: []
}

const mutations = {
    categories(state, categories) {
        state.categories = categories || []
        state.categoryTree = createTree(state.categories)
    }
}


export default {
    namespaced: true,
    state,
    mutations
}
