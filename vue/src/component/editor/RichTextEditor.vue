<template>
    <div v-loading="!hasInit" element-loading-text="正在加载编辑器..." class="tinymce-container" :style="{width}">
        <div class="tinymce-wrapper">
            <textarea :id="id" class="tinymce-textarea"/>
        </div>
    </div>
</template>

<script>
import {waitUntilSuccess} from "@/util"
import {elError} from "@/util/message"

const DEFAULT_OPTIONS = {
    language: 'zh_CN',
    language_url: `${process.env.BASE_URL}static/tinymce/langs/zh_CN.js`,
    object_resizing: false,
    end_container_on_empty_block: true,
    default_link_target: '_blank',
    convert_urls: false
}

export default {
    name: "RichTextEditor",

    props: {
        value: String,
        readonly: Boolean,
        height: {type: String, default: '400px'},
        width: {type: String, default: '100%'}
    },

    data() {
        return {
            id: 'tinymce-' + Date.now(),
            fullscreen: false,
            hasInit: false,
            manualChange: false,
        }
    },

    watch: {
        value(value) {
            if (!this.hasInit) return
            if (this.manualChange) this.manualChange = false
            else this.set(value)
        },
        readonly(value) {
            this.hasInit && this.getInstance().mode.set(value ? 'readonly' : 'design')
        }
    },

    methods: {
        getInstance() {
            return window.tinymce.get(this.id)
        },
        get() {
            return this.getInstance().getContent()
        },
        set(content) {
            this.getInstance().setContent(content || '')
        },
        init() {
            const ctx = this
            waitUntilSuccess(() => window.tinymce)
                .then(() => {
                    window.tinymce.init({
                        ...DEFAULT_OPTIONS,
                        selector: `#${ctx.id}`,
                        height: ctx.height,
                        init_instance_callback(editor) {
                            ctx.hasInit = true
                            editor.setContent(ctx.value || '')
                            editor.mode.set(ctx.readonly ? 'readonly' : 'design')
                        },
                        setup(editor) {
                            editor.on('keyup undo redo', () => {
                                ctx.manualChange = true
                                ctx.$emit('input', editor.getContent())
                            })
                            editor.on('FullscreenStateChanged', e => {
                                ctx.fullscreen = e.state
                            })
                        }
                    })
                })
                .catch(() => elError('tinymce初始化失败，请检查js是否引入成功'))
        },
        destroy() {
            const tinymce = this.getInstance()
            if (!tinymce) return

            this.fullscreen && tinymce.execCommand('mceFullScreen')

            tinymce.destroy()
        }
    },

    mounted() {
        this.init()
        this.$once('hook:beforeDestroy', this.destroy)
    }
}
</script>

<style lang="scss">
.tinymce-container {
    position: relative;

    .tinymce-wrapper {
        width: 100%;

        .tinymce-textarea {
            visibility: hidden;
        }
    }
}

.tox-tinymce-aux {
    z-index: 10000 !important;
}
</style>
