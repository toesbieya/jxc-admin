import {isLogin} from "@/util/auth"

export default {
    computed: {
        showOfflineTip() {
            return !this.$store.state.websocket.online && isLogin()
        },
    },

    watch: {
        showOfflineTip(v) {
            v ? this.$bottomTip('与服务器失去连接') : this.$bottomTip.close()
        }
    }
}
