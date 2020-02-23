<template>
    <header
            :class="{'hide-header':hideHeader}"
            class="header-container"
            @mouseenter="mouseOutside=false"
            @mouseleave="mouseOutside=true"
    >
        <v-navbar @menu-show="navbarMenuShow=$event"/>
        <tags-view @menu-show="tagsViewMenuShow=$event"/>
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
                headerAutoHidden: state => state.headerAutoHidden,
            }),
            hideHeader() {
                return this.mouseOutside
                    && this.headerAutoHidden
                    && !this.tagsViewMenuShow
                    && !this.navbarMenuShow
            },
        },
        watch: {
            hideHeader(v) {
                this.$store.commit('app/SET_HASHEADER', !v)
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
    .header-container {
        position: relative;
        height: $headerHeight;
        transition: height .3s ease-in-out;
        flex-shrink: 0;

        .awake-area {
            position: fixed;
            top: 0;
            width: 100%;
            height: 15px;
            opacity: 0;
        }

        &.hide-header {
            height: 0;
        }
    }
</style>
