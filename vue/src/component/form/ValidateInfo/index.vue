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
        <template slot="reference">
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
        onValidate(prop, success, msg) {
            const alreadyIndex = this.error.findIndex(i => i.prop === prop)
            if (alreadyIndex !== -1) {
                if (success) this.error.splice(alreadyIndex, 1)
                else this.$set(this.error, alreadyIndex, {...this.error[alreadyIndex], msg})
            }
            else {
                if (success) return
                const formItem = this.findFormItem(prop)
                this.error.push({prop, msg, label: formItem.label})
            }
        },

        onClick({prop}) {
            const formItem = this.findFormItem(prop)
            formItem && formItem.$el.scrollIntoView({block: 'center'})
            this.$refs.popover.doClose()
        },

        getFormRef() {
            let ref = this.form
            if (!ref) return
            if (typeof ref === 'function') {
                ref = ref()
            }
            return ref
        },

        findFormItem(prop) {
            const form = this.getFormRef()
            if (!form) return
            return form.fields.find(i => i.prop === prop)
        }
    },

    mounted() {
        const form = this.getFormRef()
        if (!form) return
        form.$on('validate', this.onValidate)
        this.$once('hook:beforeDestroy', () => {
            form.$off('validate', this.onValidate)
        })
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.form-validate-info {
    &.el-popper {
        padding: 0;
    }

    &-ref {
        padding: 0 10px;
        cursor: pointer;
    }

    &-title {
        text-align: center;
        padding: 5px 16px 4px;
        border-bottom: 1px solid #f0f0f0;
    }

    &-content {
        max-height: 300px;
        overflow: overlay;
    }

    &-item {
        padding: 8px 16px;
        background-color: #ffffff;
        border-bottom: 1px solid #f0f0f0;
        cursor: pointer;
        transition: background-color .3s;

        &:hover {
            background-color: #e6f7ff;
        }

        .el-icon-circle-close {
            float: left;
            margin-top: 4px;
            margin-right: 12px;
            padding-bottom: 22px;
        }

        &-label {
            margin-top: 2px;
            color: rgba(0, 0, 0, .45);
            font-size: 12px;
        }
    }

    &-ref,
    &-item .el-icon-circle-close {
        color: $--color-danger;
    }
}
</style>
