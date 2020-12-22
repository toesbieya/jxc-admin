<template>
    <el-popover
        ref="popover"
        :disabled="error.length === 0"
        placement="top"
        width="256"
        trigger="click"
        popper-class="form-validate-info"
    >
        <div class="form-validate-info-title">表单校验信息</div>
        <div class="form-validate-info-content">
            <div v-for="i in error" :key="i.prop" class="form-validate-info-item" @click="() => onClick(i)">
                <i class="el-icon-circle-close"/>
                <div class="form-validate-info-item-msg">{{ i.msg }}</div>
                <div class="form-validate-info-item-label">{{ i.label }}</div>
            </div>
        </div>
        <template v-slot:reference>
            <span v-show="error.length > 0" class="form-validate-info-ref">
                <i class="el-icon-circle-close"/>
                {{ error.length }}
            </span>
        </template>
    </el-popover>
</template>

<script>
export default {
    name: "FormValidateInfo",

    props: {form: [Function, Object]},

    data() {
        return {
            visible: false,
            error: []
        }
    },

    methods: {
        //滚动到验证不通过的表单项
        onClick({prop}) {
            const formItem = this.getFormItem(prop)
            formItem && formItem.$el.scrollIntoView({block: 'center'})
            this.$refs.popover.doClose()
        },

        //获取el-form实例
        getForm() {
            return typeof this.form === 'function' ? this.form() : this.form
        },

        //根据prop获取el-form-item实例
        getFormItem(prop) {
            const form = this.getForm()
            return form && form.fields.find(i => i.prop === prop)
        },

        //el-form触发validate事件时调用
        onValidate(prop, success, msg) {
            const alreadyIndex = this.error.findIndex(i => i.prop === prop)
            if (alreadyIndex !== -1) {
                if (success) this.error.splice(alreadyIndex, 1)
                else this.$set(this.error, alreadyIndex, {...this.error[alreadyIndex], msg})
            }
            else {
                if (success) return
                const formItem = this.getFormItem(prop)
                this.error.push({prop, msg, label: formItem.label})
            }
        }
    },

    mounted() {
        const form = this.getForm()
        if (!form) return

        form.$on('validate', this.onValidate)
        this.$once('hook:beforeDestroy', () => {
            form.$off('validate', this.onValidate)
        })
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
