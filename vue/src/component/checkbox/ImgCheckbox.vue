<script type="text/jsx">
import common from './common'

export default {
    name: "ImgCheckBox",

    mixins: [common],

    props: ['img'],

    render() {
        return (
            <div class="img-checkbox" on-click={this.click}>
                <img src={this.img}/>
                {this.checked && <i class="el-icon-check"/>}
            </div>
        )
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.img-checkbox {
    border-radius: 2px;
    cursor: pointer;
    position: relative;

    .el-icon-check {
        position: absolute;
        color: $--color-primary;
        font-weight: bold;
        top: 25px;
        right: 10px;
    }
}
</style>
