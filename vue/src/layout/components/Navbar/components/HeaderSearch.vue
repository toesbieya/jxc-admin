<template>
    <div :class="{'show':show}" class="header-search">
        <i class="el-icon-search navbar-icon" title="快捷搜索" @click.stop="click"/>

        <el-select
                ref="headerSearchSelect"
                v-model="search"
                class="header-search-select"
                :remote-method="querySearch"
                default-first-option
                filterable
                placeholder="搜索页面"
                remote
                popper-append-to-body
                @change="change"
        >
            <el-option
                    v-for="item in options"
                    :key="item.path"
                    :label="item.title.join(' > ')"
                    :value="item.path"
            />
        </el-select>
    </div>
</template>

<script>
    import Fuse from 'fuse.js'

    export default {
        name: 'HeaderSearch',

        data() {
            return {
                search: '',
                options: [],
                searchPool: [],
                show: false,
                fuse: null
            }
        },

        computed: {
            routes() {
                return this.$store.state.resource.routes
            }
        },

        watch: {
            routes() {
                this.searchPool = this.generateRoutes(this.routes)
                this.initFuse(this.searchPool)
            },

            show(value) {
                if (value) document.body.addEventListener('click', this.close)
                else document.body.removeEventListener('click', this.close)
            }
        },

        methods: {
            click() {
                this.show = !this.show
                if (this.show) this.$refs.headerSearchSelect.focus()
            },

            close() {
                this.$refs.headerSearchSelect.blur()
                this.search = ''
                this.options = []
                this.show = false
            },

            change(val) {
                this.close()
                this.$router.push(val).catch(() => ({}))
            },

            initFuse(list) {
                this.fuse = new Fuse(list, {
                    shouldSort: true,
                    threshold: 0.4,
                    location: 0,
                    distance: 100,
                    maxPatternLength: 32,
                    minMatchCharLength: 1,
                    keys: ['title']
                })
            },

            generateRoutes(routes, prefixTitle = []) {
                let res = []
                for (const route of routes) {
                    const data = {path: route.fullPath, title: [...prefixTitle]}

                    if (route.meta && route.meta.title) {
                        data.title = [...data.title, route.meta.title]
                        if (route.name) res.push(data)
                    }

                    if (route.children) {
                        const tempRoutes = this.generateRoutes(route.children, data.title)
                        if (tempRoutes.length > 0) res.push(...tempRoutes)
                    }
                }
                return res
            },

            querySearch(query) {
                this.options = query ? this.fuse.search(query) : []
            }
        },

        mounted() {
            this.searchPool = this.generateRoutes(this.routes)
            this.initFuse(this.searchPool)
        }
    }
</script>

<style lang="scss">
    .header-search {
        .header-search-select {
            font-size: 18px;
            transition: width 0.2s;
            width: 0;
            overflow: hidden;
            background: transparent;
            border-radius: 0;
            display: inline-block;
            vertical-align: middle;

            .el-input__inner {
                border-radius: 0;
                border: 0;
                padding-left: 0;
                padding-right: 0;
                box-shadow: none !important;
                border-bottom: 1px solid #d9d9d9;
                vertical-align: middle;
            }
        }

        &.show {
            .header-search-select {
                width: 210px;
                margin-left: 10px;
            }
        }
    }
</style>
