<script type="text/jsx">
import common from './common'

export default {
    name: "ColorCheckBox",

    mixins: [common],

    render() {
        return (
            <div class="color-checkbox" style={{backgroundColor: this.value}} on-click={this.click}>
                {this.checked && <i class="el-icon-check"/>}
            </div>
        )
    }
}
</script>

<style>
.color-checkbox {
    width: 20px;
    height: 20px;
    margin: 5px;
    border-radius: 2px;
    cursor: pointer;
    text-align: center;
    font-weight: bold;
    color: #ffffff;
}
</style>
