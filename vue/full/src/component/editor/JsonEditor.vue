<template>
    <textarea/>
</template>

<script>
import {isEmpty} from "@/util"

export default {
    name: "JsonEditor",

    props: ['value'],

    watch: {
        value(value) {
            const editorValue = this.jsonEditor.getValue()
            if (value !== editorValue) {
                this.jsonEditor.setValue(this.value)
            }
        }
    },

    mounted() {
        this.jsonEditor = window.CodeMirror.fromTextArea(
            this.$el,
            {
                mode: 'application/json',
                theme: 'rubyblue'
            }
        )

        !isEmpty(this.value) && this.jsonEditor.setValue(this.value)
        this.jsonEditor.on('change', cm => {
            this.$emit('input', cm.getValue())
        })
    },

    beforeDestroy() {
        const element = this.jsonEditor.doc.cm.getWrapperElement()
        element && element.remove && element.remove()
    }
}
</script>
