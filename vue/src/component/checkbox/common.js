export default {
    props: ['value'],

    computed: {
        checked() {
            const group = this.$parent
            return group && group.value === this.value
        }
    },

    methods: {
        click() {
            if (this.checked) return
            this.$parent.$emit('input', this.value)
        }
    },

    created() {
        const group = this.$parent
        group && group.children.push(this.value)
    }
}
