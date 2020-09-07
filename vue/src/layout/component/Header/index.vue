<script type="text/jsx">
import {getters as tagsViewGetters} from "@/layout/store/tagsView"
import VNavbar from './component/Navbar'
import TagsView from './component/TagsView'

export default {
    name: "Header",

    components: {VNavbar, TagsView},

    render() {
        return (
            <header class="header-container">
                <v-navbar/>
                {tagsViewGetters.enabled && <tags-view/>}
            </header>
        )
    }
}
</script>
