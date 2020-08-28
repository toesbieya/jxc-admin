<script type="text/jsx">
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
            tagsViewMenuShow: false
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
            this.$store.commit('app/hasNav', !v)
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
    },

    render() {
        const mouseCtrlFactory = (enter = true) => () => this.mouseOutside = !enter
        const menuCtrlFactory = key => e => this[key] = e

        return (
            <el-collapse-transition>
                <header
                    v-show={!this.hideHeader}
                    class="header-container"
                    on-mouseenter={mouseCtrlFactory()}
                    on-mouseleave={mouseCtrlFactory(false)}
                >
                    <v-navbar on-menu-show={menuCtrlFactory('navbarMenuShow')}/>

                    {this.useTagsView && <tags-view on-menu-show={menuCtrlFactory('tagsViewMenuShow')}/>}
                </header>
            </el-collapse-transition>
        )
    }
}
</script>
