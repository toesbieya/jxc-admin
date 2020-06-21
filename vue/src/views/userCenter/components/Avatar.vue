<template>
    <dialog-form :loading="loading" title="上传头像" :value="value" width="50%" @close="cancel">
        <el-row class="avatar-cropper">
            <div class="img-wrapper">
                <vue-cropper
                        ref="cropper"
                        :img="img"
                        :info="false"
                        autoCrop
                        autoCropHeight="200px"
                        autoCropWidth="200px"
                        fixedBox
                        full
                        outputType="png"
                        @wheel.native.prevent="scale"
                />
            </div>

            <input
                    ref="input"
                    accept="image/png, image/jpeg, image/gif, image/jpg"
                    style="display: none"
                    type="file"
                    @change="chooseImage"
            >
        </el-row>

        <template v-slot:footer>
            <el-button plain size="small" @click="$refs.input.click()">选择图片</el-button>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </dialog-form>
</template>

<script>
    import {VueCropper} from 'vue-cropper'
    import DialogForm from '@/components/DialogForm'
    import dialogMixin from "@/mixins/dialogMixin"
    import {elError, elSuccess} from "@/utils/message"
    import {autoCompleteUrl, upload} from "@/utils/file"
    import {updateAvatar} from "@/api/system/user"

    export default {
        name: "Avatar",

        mixins: [dialogMixin],

        components: {VueCropper, DialogForm},

        props: {
            value: Boolean
        },

        data() {
            return {
                loading: false,
                img: '',
                name: ''
            }
        },

        methods: {
            chooseImage(e) {
                if (this.loading) return
                this.clear()
                let file = e.target.files[0]
                if (!file.type.includes('image')) {
                    return elError('请上传图片')
                }
                if (file.size > 1048576) {
                    return elError('上传的图片不能大于1M')
                }
                this.name = file.name
                let reader = new FileReader()
                reader.onload = e => {
                    this.img = window.URL.createObjectURL(new Blob([e.target.result]))
                }
                reader.readAsArrayBuffer(file)
            },

            confirm() {
                if (!this.img) return elError('请先上传图片')
                if (this.loading) return
                this.loading = true
                this.$refs.cropper.getCropBlob(data => {
                    upload(new Blob([data]), this.name)
                        .then(({key}) => updateAvatar(key))
                        .then(({key, msg}) => {
                            this.$store.commit('user/avatar', autoCompleteUrl(key))
                            this.$store.dispatch('user/refresh')
                            elSuccess(msg)
                        })
                        .finally(() => this.cancel())
                })
            },

            clear() {
                this.loading = false
                this.$refs.cropper.clearCrop()
                if (this.img) window.URL.revokeObjectURL(this.img)
                this.img = ''
                this.name = ''
            },

            scale(e) {
                const eventDelta = e.wheelDelta || -(e.detail || 0) * 40
                this.$refs.cropper.changeScale(eventDelta / 120)
            },

            cancel() {
                this.closeDialog()
                this.clear()
            }
        }
    }
</script>

<style lang="scss">
    .avatar-cropper {
        .cropper-crop-box {
            border: 1px solid #39f;
            border-radius: 50%;
            overflow: hidden;
        }

        .img-wrapper {
            height: 360px;
            width: 640px;
            display: inline-block;
            border: 1px solid #ebebeb;
        }
    }
</style>
