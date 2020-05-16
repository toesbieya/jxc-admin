<template>
    <transition name="el-message-fade" @after-leave="handleAfterLeave">
        <div v-show="visible"
             :class="messageClass"
             :style="positionStyle"
             role="alert"
             @mouseenter="clearTimer"
             @mouseleave="startTimer"
        >
            <div v-if="badge>1" :key="badge" class="el-message__badge">{{badge}}</div>
            <i v-if="iconClass" :class="iconClass"/>
            <i v-else :class="typeClass"/>
            <slot>
                <p v-if="!dangerouslyUseHTMLString" class="el-message__content">{{ message }}</p>
                <p v-else v-html="message" class="el-message__content"/>
            </slot>
            <i v-if="showClose" class="el-message__closeBtn el-icon-close" @click="close"/>
        </div>
    </transition>
</template>

<script>
    export default {
        data() {
            return {
                visible: false,
                badge: 1,
                message: '',
                duration: 3000,
                type: '',
                iconClass: '',
                customClass: '',
                onClose: null,
                showClose: false,
                closed: false,
                verticalOffset: 20,
                timer: null,
                dangerouslyUseHTMLString: false,
                center: false
            }
        },
        computed: {
            messageClass() {
                return [
                    'el-message',
                    this.type && !this.iconClass ? `el-message--${this.type}` : '',
                    this.center ? 'is-center' : '',
                    this.showClose ? 'is-closable' : '',
                    this.customClass
                ]
            },
            typeClass() {
                return this.type && !this.iconClass
                    ? `el-message__icon el-icon-${this.type}`
                    : ''
            },
            positionStyle() {
                return {
                    'top': `${this.verticalOffset}px`
                }
            }
        },
        watch: {
            closed(newVal) {
                if (newVal) {
                    this.visible = false
                }
            }
        },
        methods: {
            handleAfterLeave() {
                this.$destroy(true)
                this.$el.parentNode.removeChild(this.$el)
            },
            close() {
                this.closed = true
                typeof this.onClose === 'function' && this.onClose(this)
            },
            clearTimer() {
                clearTimeout(this.timer)
            },
            startTimer() {
                if (this.duration > 0) {
                    this.timer = setTimeout(() => {
                        !this.closed && this.close()
                    }, this.duration)
                }
            }
        },
        mounted() {
            this.startTimer()
        }
    }
</script>
