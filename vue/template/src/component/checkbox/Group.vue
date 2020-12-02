<script type="text/jsx">
export default {
    name: "CheckboxGroup",

    props: {value: String},

    data: () => ({children: []}),

    render() {
        return <div class="checkbox-group">{this.$slots.default}</div>
    }
}
</script>

<style>
.checkbox-group {
    display: flex;
    flex-wrap: wrap;
    width: 100%;
}
</style>
