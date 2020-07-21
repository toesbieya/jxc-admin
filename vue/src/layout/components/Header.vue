<template>
    <header
            :class="{'hide-header':hideHeader}"
            class="header-container"
            @mouseenter="() => valueCtrl('mouseOutside',false)"
            @mouseleave="() => valueCtrl('mouseOutside',true)"
    >
        <v-navbar @menu-show="e => valueCtrl('navbarMenuShow',e)"/>

        <tags-view v-if="useTagsView" @menu-show="e => valueCtrl('tagsViewMenuShow',e)"/>
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
            useTagsView(v) {
                !v && this.$store.dispatch('tagsView/delAllViews')
            },

            hideHeader(v) {
                this.$store.commit('app/hasHeader', !v)
                v ? this.addEvent() : this.removeEvent()
            }
        },

        methods: {
            valueCtrl(key, value) {
                this[key] = value
            },

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
