<template>
    <main class="app-main">
        <el-scrollbar ref="scrollbar" v-show="!showIframe" class="scroll-container">
            <div class="page-view">
                <keep-router-view-alive :include="cachedViews">
                    <transition :name="transitionName" mode="out-in">
                        <router-view/>
                    </transition>
                </keep-router-view-alive>
            </div>
        </el-scrollbar>

        <!--跨域iframe无法调整高度，只能使用原生滚动条-->
        <iframe
            v-for="src in iframeList"
            v-show="showIframe && src === currentIframe"
            :id="src"
            :key="src"
            :src="src"
            frameborder="0"
            height="100%"
            width="100%"
        />

        <el-backtop v-if="showBackToTop" target=".app-main .el-scrollbar__wrap" :visibility-height="400" :bottom="66"/>
    </main>
</template>

<script>
import Vue from 'vue'
import {mapState} from 'vuex'
import KeepRouterViewAlive from "./KeepAlive"
import Footer from "./Footer"

export default {
    name: 'AppMain',

    components: {KeepRouterViewAlive},

    computed: {
        ...mapState('app', ['scrollTop']),

        ...mapState('setting', ['showBackToTop']),

        ...mapState('tagsView', ['cachedViews', 'transitionName']),

        ...mapState('iframe', {
            showIframe: state => state.show,
            currentIframe: state => state.current,
            iframeList: state => state.list
        })
    },

    watch: {
        scrollTop(v) {
            if (v >= 0) this.$refs.scrollbar.$refs.wrap.scrollTop = v
        }
    },

    mounted() {
        //插入footer
        const FooterConstructor = Vue.extend(Footer)
        const footerInstance = new FooterConstructor().$mount()
        this.$refs.scrollbar.$refs.wrap.appendChild(footerInstance.$el)
    }
}
</script>

<style lang="scss">
@import "~@/assets/styles/variables.scss";

.app-main {
    position: relative;
    overflow: hidden;
    background-color: #f5f7f9;
    flex: 1;

    .scroll-container {
        position: relative;
        height: 100%;

        .page-view {
            margin: $page-view-margin;
        }

        > .el-scrollbar__bar.is-horizontal {
            display: none !important;
        }

        > .el-scrollbar__wrap {
            display: flex;
            flex-direction: column;
            overflow-x: hidden;

            .el-scrollbar__view {
                flex: 1;
            }
        }
    }
}
</style>
