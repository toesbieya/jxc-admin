<template>
    <div :class="{'collapse':collapse}" class="sidebar-logo-container">
        <router-link class="sidebar-logo-link" key="expand" to="/">
            <img v-if="logo" :src="logo" class="sidebar-logo">
            <h1 v-if="!collapse" class="sidebar-title">{{ title }}</h1>
        </router-link>
    </div>
</template>

<script>
    import {sidebarLogoUrl, title} from '@/config'

    export default {
        name: 'SidebarLogo',

        props: {collapse: Boolean},

        data() {
            return {
                title,
                logo: sidebarLogoUrl
            }
        }
    }
</script>
