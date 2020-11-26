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

    //主题，light 或 dark
    theme: 'light',

    //手风琴效果
    uniqueOpen: true,

    //是否折叠
    collapse: false,

    //折叠时显示上级
    showParentOnCollapse: false,

    //自动隐藏
    autoHide: false,

    //汉堡包的位置，aside 或 head
    hamburgerPosition: 'aside'
}

const store = Vue.observable(state)

export const getters = createGetters(store)

export const mutations = bindThis({
    ...createMutations(store),

    /*移动端或设置了侧边栏自动隐藏时打开关闭抽屉，否则展开折叠*/
    open() {
        if (appGetters.isMobile || store.autoHide) {
            store.show = true
        }
        else store.collapse = false
    },
    close() {
        if (appGetters.isMobile || store.autoHide) {
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
                if (appGetters.isMobile) {
                    open = !store.show
                }
                else open = store.collapse
                return open ? this.open() : this.close()
        }
    }
})
