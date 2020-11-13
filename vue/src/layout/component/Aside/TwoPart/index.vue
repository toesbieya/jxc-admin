<script type="text/jsx">
import RootSidebar from './root'
import SubSidebar from './sub'

export default {
    functional: true,

    render() {
        return (
            <aside class="aside">
                <RootSidebar/>
                <SubSidebar/>
            </aside>
        )
    }
}
</script>
