<template>
    <div v-loading="config.loading" class="message-stream">
        <el-button
                v-if="showReadAllBtn>0"
                plain
                size="mini"
                style="margin-bottom: 20px"
                @click="readAll"
        >
            清除所有未读
        </el-button>
        <empty v-if="tableData.length===0"/>
        <template v-else>
            <el-timeline>
                <el-timeline-item
                        v-for="msg in tableData"
                        :key="msg.index"
                        :timestamp="msg.time"
                        placement="top"
                >
                    <el-card
                            v-for="child in msg.children"
                            :key="child.id"
                            :class="{'message-stream__item':true,viewed:child.read}"
                            @click.native="read(child)"
                    >
                        <div class="message-stream__item__title">
                            <span v-if="!child.read" class="dot success"/>
                            <b>【{{transformType(child.type)}}】</b>
                            {{child.title}}
                        </div>
                        <div v-html="child.content"/>
                        <div class="message-stream__item__footer">by: {{child.pname}}</div>
                    </el-card>
                </el-timeline-item>
            </el-timeline>

            <el-pagination
                    background
                    :current-page="searchForm.page"
                    :page-size="searchForm.pageSize"
                    :total="searchForm.total"
                    hide-on-single-page
                    layout="prev, pager, next"
                    @current-change="pageChange"
            />
        </template>
    </div>
</template>

<script>
    import tablePageMixin from '@/mixins/tablePageMixin'
    import Empty from '@/components/Empty'
    import {search, read, readAll} from "@/api/message/user"
    import {isEmpty} from "@/utils"

    export default {
        name: "MessageStream",
        mixins: [tablePageMixin],
        components: {Empty},
        props: {
            mode: String,
        },
        data() {
            return {
                searchForm: {
                    pageSize: 30
                },
            }
        },
        computed: {
            unreadCount() {
                return this.$store.state.message.unreadCount
            },
            showReadAllBtn() {
                return this.mode === 'unread' && this.unreadCount > 0
            }
        },
        watch: {
            mode() {
                this.search()
            }
        },
        methods: {
            search() {
                if (this.config.loading) return
                this.config.loading = true
                search({...this.searchForm, unread: this.mode === 'unread'})
                    .then(({list, total, data}) => {
                        !isEmpty(data) && this.$store.commit('message/unreadCount', data)
                        this.searchForm.total = total
                        this.tableData = this.transformList(list)
                    })
                    .finally(() => this.config.loading = false)
            },

            //按照发布时间分组，精确到日
            transformList(list) {
                const thisYear = new Date().getFullYear()
                const map = {}
                let index = 0

                list.forEach(item => {
                    let time = new Date(item.ptime),
                        year = time.getFullYear(),
                        month = time.getMonth() + 1,
                        day = time.getDate()
                    item.read = this.mode === 'read'
                    time = (year === thisYear ? '' : `${year}年`) + `${month}月${day}日`
                    const already = map[time]
                    if (!already) {
                        map[time] = {index: index++, time, children: [item]}
                    }
                    else already.children.push(item)
                })

                const result = []

                Object.keys(map).forEach(key => result[map[key].index] = map[key])

                return result
            },

            read(msg) {
                if (msg.read) return
                msg.read = true
                this.$store.commit('message/unreadCount', this.unreadCount - 1)
                read(msg.id)
            },

            readAll() {
                readAll().then(this.search)
            },

            transformType(type) {
                switch (type) {
                    case 0:
                        return '通知提醒'
                    case 1:
                        return '系统公告'
                }
            },
        }
    }
</script>

<style lang="scss">
    .message-stream {
        &__item {
            cursor: pointer;
            position: relative;
            color: #666;
            background: #FFF9EA;

            & + & {
                margin-top: 20px;
            }

            &.viewed {
                cursor: auto;
                background: transparent;

                &::before {
                    visibility: hidden;
                }
            }

            &__title {
                text-align: left;

                .dot {
                    height: 8px;
                    width: 8px;
                    margin-right: 0;
                }
            }

            &__footer {
                text-align: right;
            }
        }
    }
</style>
