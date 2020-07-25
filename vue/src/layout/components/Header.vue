<template>
    <header
            :class="{'header-container':true,'hide-header':hideHeader}"
            @mouseenter="() => this.mouseOutside = false"
            @mouseleave="() => this.mouseOutside = true"
    >
        <v-navbar @menu-show="e => this.navbarMenuShow = e"/>

        <tags-view v-if="useTagsView" @menu-show="e => this.tagsViewMenuShow = e"/>
    </header>
</template>

<script>
    import {mapState} from 'vuex'
    import VNavbar from './Navbar'
    import TagsView from './TagsView'

    export default {
        name: "Header",

        components: {VNavbar, TagsView},

        data() {
            return {
                mouseOutside: true,
                navbarMenuShow: false,
                tagsViewMenuShow: false,
                appMain: null
            }
        },

        computed: {
            ...mapState('setting', ['useTagsView', 'headerAutoHidden']),

            hideHeader() {
                return this.mouseOutside
                    && this.headerAutoHidden
                    && !this.tagsViewMenuShow
                    && !this.navbarMenuShow
            },
        },

        watch: {
            //多页签停用时清除所有页面缓存
            useTagsView(v) {
                !v && this.$store.dispatch('tagsView/delAllViews')
            },

            hideHeader(v) {
                this.$store.commit('app/hasHeader', !v)
                v ? this.addEvent() : this.removeEvent()
            }
        },

        methods: {
            moveEvent(e) {
                if (e.clientY <= 15) this.mouseOutside = false
            },

            addEvent() {
                this.appMain.addEventListener('mousemove', this.moveEvent)
            },

            removeEvent() {
                this.appMain.removeEventListener('mousemove', this.moveEvent)
            }
        },

        mounted() {
            this.appMain = document.querySelector('.app-main')
            if (this.headerAutoHidden) this.addEvent()

            this.$once('hook:beforeDestroy', this.removeEvent)
        }
    }
</script>

<style lang="scss">
    @import "~@/assets/styles/variables.scss";

    .has-tags-view .header-container {
        height: calc(#{$nav-height} + #{$tags-view-height});
    }

    .header-container {
        position: relative;
        height: $nav-height;
        transition: height .3s ease-in-out;
        flex-shrink: 0;

        &.hide-header {
            height: 0;

            > .navbar {
                height: 0;
            }
        }
    }
</style>
