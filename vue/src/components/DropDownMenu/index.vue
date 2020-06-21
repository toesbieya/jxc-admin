<template>
    <div :class="{active:isActive}" class="share-dropdown-menu">
        <div class="share-dropdown-menu-wrapper">
            <span class="share-dropdown-menu-title" @click.self="clickTitle">{{ title }}</span>

            <div v-for="(item,index) of items" :key="index" class="share-dropdown-menu-item">
                <a v-if="item.href" :href="item.href" target="_blank">{{ item.title }}</a>
                <span v-else>{{ item.title }}</span>
            </div>
        </div>
    </div>
</template>

<script>
    //参考drop.js
    export default {
        name: 'DropdownMenu',

        props: {
            items: {
                type: Array,
                default: function () {
                    return []
                }
            },
            title: {
                type: String,
                default: 'vue'
            }
        },

        data() {
            return {
                isActive: false
            }
        },

        methods: {
            clickTitle() {
                this.isActive = !this.isActive
            }
        }
    }
</script>

<style lang="scss" src="./style.scss"></style>
