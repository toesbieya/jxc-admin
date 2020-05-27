export default {
    methods: {
        closeDialog() {
            this.$emit('input', false)
        }
    }
}
