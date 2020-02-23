/*
* 数据缓存
* 比如商品分类信息等等
* */
import {regionDataUrl} from '@/config'
import {createTree} from '@/utils/tree'

const state = {
    categories: [],
    categoryTree: [],
    regionTree: []
}

const mutations = {
    SET_CATEGORIES(state, categories) {
        state.categories = categories || []
        state.categoryTree = createTree(state.categories)
    },
    SET_REGION_TREE(state, regionTree) {
        state.regionTree = regionTree || []
    },
}

const actions = {
    initRegion({commit}) {
        return fetch(regionDataUrl)
            .then(r => r.json())
            .then(r => commit('SET_REGION_TREE', r))
    }
}


export default {
    namespaced: true,
    state,
    mutations,
    actions
}
