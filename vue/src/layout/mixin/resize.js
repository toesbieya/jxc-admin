import {debounce} from '@/util'
import {isMobile} from "@/util/browser"

export default {
    methods: {
        $_resizeHandler() {
            if (!document.hidden) {
                const mobile = isMobile()
                this.$store.commit('app/device', mobile ? 'mobile' : 'pc')
                mobile && this.$store.commit('setting/sidebarCollapse', true)
            }
        }
    },
    mounted() {
        this.$_resizeHandler = debounce(this.$_resizeHandler)
        this.$_resizeHandler()

        window.addEventListener('resize', this.$_resizeHandler)

        this.$once('hook:beforeDestroy', () => {
            window.removeEventListener('resize', this.$_resizeHandler)
        })
    }
}
