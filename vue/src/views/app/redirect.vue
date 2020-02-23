<script>
    export default {
        created() {
            const {params, query} = this.$route
            if ('params' in query) {
                let {name, params} = JSON.parse(query.params)
                this.$router.replace({name, params})
            }
            else this.$router.replace({path: '/' + params.path, query})
        },
        render: h => h()
    }
</script>
