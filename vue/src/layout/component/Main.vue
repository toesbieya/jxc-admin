<template>
    <main class="app-main">
        <div v-show="!showIframe" class="scroll-container">
            <div class="page-view">
                <keep-router-view-alive :include="cachedViews">
                    <transition :name="transitionName" mode="out-in">
                        <router-view/>
                    </transition>
                </keep-router-view-alive>
            </div>
            <v-footer/>
        </div>

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

        <el-backtop
            v-if="showBackToTop"
            target=".app-main .scroll-container"
            :visibility-height="400"
            :bottom="66"
        >
            <div class="back-to-top">
                <v-icon icon="el-icon-top"/>
            </div>
        </el-backtop>
    </main>
</template>

<script>
import {mapState} from 'vuex'
import KeepRouterViewAlive from "./KeepAlive"
import VFooter from "./Footer"

export default {
    name: 'AppMain',

    components: {KeepRouterViewAlive, VFooter},

    computed: {
        ...mapState('setting', ['showBackToTop']),

        ...mapState('tagsView', ['cachedViews', 'transitionName']),

        ...mapState('iframe', {
            showIframe: state => state.show,
            currentIframe: state => state.current,
            iframeList: state => state.list
        })
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.app-main {
    position: relative;
    flex: 1;
    overflow: hidden;
    background-color: #f5f7f9;

    .scroll-container {
        position: relative;
        height: 100%;
        display: flex;
        flex-direction: column;
        overflow-y: overlay;
        overflow-x: inherit;

        .page-view {
            margin: $page-view-margin;
            flex: 1;
        }
    }

    .back-to-top {
        height: 100%;
        width: 100%;
        background-color: #f2f5f6;
        box-shadow: 0 0 6px rgba(0, 0, 0, .12);
        text-align: center;
        line-height: 40px;
        color: #1989fa;
    }
}
</style>
