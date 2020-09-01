<script type="text/jsx">
export default {
    data: () => ({show: false}),

    render() {
        return (
            <transition name="el-fade-in-linear">
                {this.show && <div class="aside-hidden-mask" on-click={this.onClick}/>}
            </transition>
        )
    }
}
</script>
