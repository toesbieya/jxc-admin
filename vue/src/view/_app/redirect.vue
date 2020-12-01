<script>
import {replaceAll} from "@/util"

export default {
    mounted() {
        const {fullPath} = this.$route
        this.$router.replace(`${replaceAll(fullPath, '/redirect', '')}`)
    },

    render: h => h()
}
</script>
