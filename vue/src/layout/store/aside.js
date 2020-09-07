/**
 * 侧边栏的响应式数据
 */
import Vue from 'vue'
import {bindThis} from "@/util"
import {createGetters, createMutations} from "@/util/observable"
import {getters as appGetters} from "./app"

const state = {
    //抽屉模式时的显隐
    show: false,

    //显示顶部logo
    showLogo: true,

    //手风琴效果
    uniqueOpen: true,

    //是否折叠
    collapse: false,

    //折叠时显示上级
    showParentOnCollapse: false,

    //自动隐藏
    autoHide: false
}

const store = Vue.observable(state)

export const getters = createGetters(store)

export const mutations = bindThis({
    ...createMutations(store),

    /*折叠和自动隐藏不能同时启用*/
    collapse(v) {
        if (v) store.autoHide = false
        store.collapse = v
    },
    autoHide(v) {
        if (v) store.collapse = false
        store.autoHide = v
    },

    /*移动端是打开关闭抽屉，桌面端是展开折叠*/
    open() {
        if (appGetters.device === 'mobile') {
            store.show = true
        }
        else store.collapse = false
    },
    close() {
        if (appGetters.device === 'mobile') {
            store.show = false
        }
        else store.collapse = true
    },
    //切换侧边栏的状态
    switch(action) {
        switch (action) {
            case 'open':
                return this.open()
            case 'close':
                return this.close()
            default :
                let open = true
                if (appGetters.device === 'mobile') {
                    open = !store.show
                }
                else open = store.collapse
                return open ? this.open() : this.close()
        }
    }
})
