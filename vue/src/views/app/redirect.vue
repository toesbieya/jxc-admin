<script>
    export default {
        created() {
            const {params, query} = this.$route
            //如果使用了name+params传参
            if ('params' in query) {
                const {name, params} = JSON.parse(query.params)
                this.$router.replace({name, params})
            }
            else this.$router.replace({path: `/${params.path}`, query})
        },

        render: h => h()
    }
</script>
