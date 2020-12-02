import {isEmpty} from "@/util"

export default {
    computed: {
        showOfflineTip() {
            const state = this.$store.state
            const isLogin = !isEmpty(state.user.id, state.user.token)
            return !state.socket.online && isLogin
        },
    },

    watch: {
        showOfflineTip(v) {
            v ? this.$bottomTip('与服务器失去连接') : this.$bottomTip.close()
        }
    }
}
