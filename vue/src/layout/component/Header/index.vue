<script type="text/jsx">
import {getters as settingGetters} from "@/layout/store/setting"
import {mutations as tagsViewMutations} from "@/layout/store/tagsView"
import VNavbar from './component/Navbar'
import TagsView from './component/TagsView'

export default {
    name: "Header",

    components: {VNavbar, TagsView},

    computed: {
        useTagsView: () => settingGetters.useTagsView
    },

    watch: {
        //多页签停用时清除所有页面缓存
        useTagsView(v) {
            !v && tagsViewMutations.delAllViews()
        }
    },

    render() {
        return (
            <header class="header-container">
                <v-navbar/>
                {this.useTagsView && <tags-view/>}
            </header>
        )
    }
}
</script>
