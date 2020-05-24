import {debounce} from '@/utils'

const {body} = document
const WIDTH = 768

function $_isMobile() {
    const rect = body.getBoundingClientRect()
    return rect.width - 1 < WIDTH
}

export default {
    methods: {
        $_resizeHandler() {
            if (!document.hidden) {
                const isMobile = $_isMobile()
                this.$store.commit('app/device', isMobile ? 'mobile' : 'pc')
                isMobile && this.$store.commit('setting/sidebarCollapse', true)
            }
        }
    },
    mounted() {
        this.$_resizeHandler = debounce(this.$_resizeHandler)
        window.addEventListener('resize', this.$_resizeHandler)
        this.$_resizeHandler()
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.$_resizeHandler)
    }
}
