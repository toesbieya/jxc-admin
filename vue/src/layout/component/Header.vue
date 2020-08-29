<script type="text/jsx">
import VNavbar from './Navbar'
import TagsView from './TagsView'
import {mutations as mainMutations} from "@/layout/store/main"
import {getters as settingGetters} from "@/layout/store/setting"
import {mutations as tagsViewMutations} from "@/layout/store/tagsView"

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
        useTagsView: () => settingGetters.useTagsView,
        headerAutoHidden: () => settingGetters.headerAutoHidden,

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
            !v && tagsViewMutations.delAllViews()
        },

        hideHeader(v) {
            mainMutations.hasNav(!v)
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
