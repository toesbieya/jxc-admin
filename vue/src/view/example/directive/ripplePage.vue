<template>
    <div>
        <div class="tip-row">
            波纹，详情请见<a href="https://quasar.dev/vue-directives/material-ripple" target="_blank">quasar</a>
        </div>

        <div v-ripple.early class="ripple-example" :style="style">点我触发</div>
    </div>
</template>

<script>
export default {
    name: "ripplePage",

    data() {
        return {
            index: 0,
            colors: ['amber', 'orange', 'accent', 'lime', 'cyan', 'purple', 'brown', 'blue']
        }
    },

    computed: {
        style() {
            return {'background-color': this.colors[this.index]}
        }
    },

    mounted() {
        this.timer = window.setInterval(() => {
            this.index = (this.index + 1) % this.colors.length
            this.color = this.colors[this.index]
        }, 3000)

        this.$once('hook:beforeDestroy', () => {
            window.clearInterval(this.timer)
        })
    }
}
</script>

<style scoped>
.ripple-example {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;
    margin-top: 50px;
    height: 150px;
    width: 80%;
    max-width: 500px;
    border-radius: 3px;
    cursor: pointer;
    transition: background 1.5s;
    color: #ffffff;
    user-select: none;
}
</style>
