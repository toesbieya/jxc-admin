<template>
    <abstract-dialog :loading="loading" title="上传头像" :value="value" width="50%" @close="close">
        <div class="avatar-cropper cropper-bg">
            <div v-show="!img" class="avatar-cropper-bg cropper-bg" title="选择图片" @click="handleBgClick"/>

            <img :src="img">

            <input
                ref="input"
                accept="image/png, image/jpeg, image/gif, image/jpg"
                type="file"
                @change="onInputChange"
            >
        </div>

        <template v-slot:footer>
            <el-button plain size="small" @click="chooseImage">选择图片</el-button>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button :loading="loading" size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </abstract-dialog>
</template>

<script>
import AbstractDialog from '@/component/abstract/Dialog'
import dialogMixin from "@/mixin/dialogMixin"
import {elError, elSuccess} from "@/util/message"
import {autoCompleteUrl, upload} from "@/util/file"
import {updateAvatar} from "@/api/account"

export default {
    name: "Avatar",

    mixins: [dialogMixin],

    components: {AbstractDialog},

    props: {
        value: Boolean
    },

    data() {
        return {
            loading: false,
            img: '',
            name: '',
            type: ''
        }
    },

    methods: {
        //在未选择图片时点击背景，则弹出文件选择框
        handleBgClick() {
            !this.img && this.chooseImage()
        },

        close() {
            this.closeDialog()
            this.clear()
        },
        clear() {
            this.loading = false
            this.cropper && this.cropper.destroy()
            this.img && window.URL.revokeObjectURL(this.img)
            this.img = ''
            this.name = ''
            this.type = ''
        },

        onInputChange(e) {
            if (this.loading) return

            this.clear()

            const file = e.target.files[0]

            if (!file) return

            if (!file.type.includes('image')) {
                return elError('请上传图片')
            }

            if (file.size > 1048576) {
                return elError('上传的图片不能大于1M')
            }

            this.name = file.name
            this.type = file.type
            this.img = window.URL.createObjectURL(file)

            //使用nextTick原因是img的src此时还未赋值
            this.$nextTick(() => {
                this.cropper = new window.Cropper(
                    this.$el.querySelector('.avatar-cropper > img'),
                    {
                        dragMode: 'move',
                        initialAspectRatio: 1,
                        aspectRatio: 1,
                        checkCrossOrigin: false,
                        checkOrientation: false,
                        guides: false,
                        center: false,
                        rotatable: false,
                        cropBoxResizable: false,
                        toggleDragModeOnDblclick: false,

                        //图片加载完成后将裁剪框的大小设置为200x200并居中
                        ready: () => {
                            const size = 200
                            const container = this.$el.querySelector('.avatar-cropper')
                            const top = (500 - size) / 2
                            const left = (container.offsetWidth - size) / 2

                            this.cropper.setCropBoxData({width: size, height: size, top, left})
                        }
                    }
                )
            })
        },

        chooseImage() {
            this.$refs.input.click()
        },
        confirm() {
            if (!this.img) return elError('请先上传图片')

            if (this.loading) return

            this.loading = true

            this.cropper
                .getCroppedCanvas()
                .toBlob(
                    data => {
                        upload(data, this.name)
                            .then(({key}) => updateAvatar.request(key))
                            .then(({data, msg}) => {
                                this.$store.commit('user/avatar', autoCompleteUrl(data))
                                this.$store.dispatch('user/refresh')
                                elSuccess(msg)
                                this.closeDialog()
                            })
                            .finally(() => this.loading = false)
                    },
                    this.type
                )
        }
    },

    beforeDestroy() {
        this.clear()
    }
}
</script>

<style lang="scss">
.avatar-cropper {
    height: 500px;
    width: 100%;

    &-bg {
        height: 100%;
        width: 100%;
        cursor: pointer;
    }

    > img {
        display: none;
        height: 100%;
        width: 100%;
    }

    > input[type=file] {
        display: none;
    }

    .cropper-view-box {
        border-radius: 50%;
        box-sizing: border-box;
        border: 1px solid rgba(51, 153, 255, .75);
        outline: unset;
    }

    .cropper-face {
        border-radius: 50%;
    }
}
</style>
