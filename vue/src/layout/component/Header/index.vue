<script type="text/jsx">
import {getters as tagsViewGetters} from "@/layout/store/tagsView"
import VNavbar from './Navbar'
import TagsView from './TagsView'

export default {
    functional: true,

    render() {
        return (
            <header>
                <VNavbar/>
                {tagsViewGetters.enabled && <TagsView/>}
            </header>
        )
    }
}
</script>
