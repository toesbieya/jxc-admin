<template>
    <transition name="el-message-fade" @after-leave="handleAfterLeave">
        <div
            v-show="visible"
            :class="messageClass"
            :style="positionStyle"
            role="alert"
            @mouseenter="clearTimer"
            @mouseleave="startTimer"
        >
            <i v-if="iconClass" :class="iconClass"/>
            <i v-else :class="typeClass"/>
            <slot>
                <p v-if="!dangerouslyUseHTMLString" class="el-message__content">{{ message }}</p>
                <p v-else v-html="message" class="el-message__content"/>
            </slot>
            <i v-if="showClose" class="el-message__closeBtn el-icon-close" @click="close"/>
            <div v-if="badge>1" :key="'badge'+badge" class="el-message__badge">{{ badge }}</div>
            <div v-if="progress&&!closed" :key="'progress'+badge" class="el-message__progress" :style="progressStyle"/>
        </div>
    </transition>
</template>

<script>
export default {
    data() {
        return {
            visible: false,
            badge: 1,//标记值，大于1时显示
            progress: true,//是否显示进度条
            pause: false,//是否暂停关闭
            message: '',
            duration: 3000,
            startAt: 0,//上一次开始关闭计时的时间
            remainTime: 0,//还剩多少毫秒关闭
            type: '',//默认值改为js控制
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
        },
        progressStyle() {
            if (!this.progress || this.closed) return 'transform: scaleX(0)'
            if (this.pause) return `transform: scaleX(${this.remainTime / this.duration});animation-play-state:paused`
            return `animation-duration: ${this.duration}ms;animation-play-state:running`
        }
    },

    watch: {
        closed(newVal) {
            if (newVal) this.visible = false
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
        clearTimer(reset) {
            this.pause = true
            this.remainTime = reset === true ? this.duration : this.remainTime - (Date.now() - this.startAt)
            window.clearTimeout(this.timer)
        },
        startTimer() {
            if (this.duration > 0 && !this.closed) {
                this.pause = false
                this.startAt = Date.now()
                if (this.remainTime === 0) this.remainTime = this.duration
                this.timer = window.setTimeout(() => !this.closed && this.close(), this.remainTime)
            }
        }
    },

    mounted() {
        this.startTimer()
    }
}
</script>
