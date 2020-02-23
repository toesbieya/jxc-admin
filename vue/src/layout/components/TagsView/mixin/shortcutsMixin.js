export default {
    methods: {
        gotoViewFront() {
            if (this.visitedViews.length <= 1) return
            let index = this.visitedViews.findIndex(view => view.path === this.$route.path)
            if (index < 0) return
            if (index === 0) index = this.visitedViews.length
            return this.$router.push({path: this.visitedViews[index - 1].path})
        },
        gotoViewBehind() {
            if (this.visitedViews.length <= 1) return
            let index = this.visitedViews.findIndex(view => view.path === this.$route.path)
            if (index < 0) return
            if (index + 1 > this.visitedViews.length - 1) index = -1
            return this.$router.push({path: this.visitedViews[index + 1].path})
        },

        //快捷键监听
        shortcutsListen(e) {
            if (e.ctrlKey) {
                switch (e.key) {
                    //ctrl + →
                    case 'ArrowRight':
                        e.preventDefault()
                        return this.gotoViewBehind()
                    //ctrl + ←
                    case 'ArrowLeft':
                        e.preventDefault()
                        return this.gotoViewFront()
                }
            }
        }
    },
    mounted() {
        window.addEventListener('keydown', this.shortcutsListen)
    },
    beforeDestroy() {
        window.removeEventListener('keydown', this.shortcutsListen)
    }
}
