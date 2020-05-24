const {body} = document
const WIDTH = 768

export default {
    methods: {
        $_isMobile() {
            const rect = body.getBoundingClientRect()
            return rect.width - 1 < WIDTH
        },
        $_resizeHandler() {
            if (!document.hidden) {
                const isMobile = this.$_isMobile()
                this.$store.commit('app/device', isMobile ? 'mobile' : 'pc')
                isMobile && this.$store.commit('setting/sidebarCollapse', true)
            }
        }
    },
    mounted() {
        window.addEventListener('resize', this.$_resizeHandler)
        this.$_resizeHandler()
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.$_resizeHandler)
    }
}
