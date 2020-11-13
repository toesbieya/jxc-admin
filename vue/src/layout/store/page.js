/**
 * 路由页面的响应式数据
 */
import Vue from 'vue'
import {bindThis} from "@/util"
import {createGetters, createMutations} from "@/util/observable"

const state = {
    /*iframe的控制*/
    showIframe: false,
    currentIframe: '',
    iframeList: [],

    //是否显示侧边栏或顶部导航栏的logo
    showLogo: true,
    //logo位置，侧边栏（'aside'）、顶部导航栏（'head）
    logoPosition: 'aside',
    //是否显示页头
    showPageHeader: true,
    //是否显示返回顶部按钮
    showBackToTop: true
}

const store = Vue.observable(state)

export const getters = createGetters(store)

export const mutations = bindThis({
    ...createMutations(store),

    addIframe(src) {
        !store.iframeList.includes(src) && store.iframeList.push(src)
    },
    delIframe(src) {
        const index = store.iframeList.findIndex(i => i === src)
        index > -1 && store.iframeList.splice(index, 1)
    },
    openIframe({src}) {
        store.showIframe = true
        store.currentIframe = src
        this.addIframe(src)
    },
    closeIframe({src, del}) {
        store.showIframe = false
        store.currentIframe = ''
        del && this.delIframe(src)
    }
})
