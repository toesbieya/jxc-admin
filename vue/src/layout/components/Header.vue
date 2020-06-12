<template>
    <header
            :class="{'hide-header':hideHeader}"
            class="header-container"
            @mouseenter="mouseOutside=false"
            @mouseleave="mouseOutside=true"
    >
        <v-navbar @menu-show="navbarMenuShow=$event"/>
        <tags-view v-if="useTagsView" @menu-show="tagsViewMenuShow=$event"/>
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
            ...mapState('setting', {
                useTagsView: state => state.useTagsView,
                headerAutoHidden: state => state.headerAutoHidden
            }),
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
        },
        beforeDestroy() {
            this.removeEvent()
        }
    }
</script>
<style lang="scss">
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
        }
    }
</style>
