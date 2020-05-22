<template>
    <el-row class="avatar-cropper" type="flex">
        <div class="img-wrapper">
            <vue-cropper
                    ref="cropper"
                    :img="img"
                    :info="false"
                    autoCrop
                    autoCropHeight="200px"
                    autoCropWidth="200px"
                    centerBox
                    fixedBox
                    full
                    outputType="png"
                    @realTime="previews=$event"
                    @wheel.native.prevent="scale"
            />
        </div>
        <div class="operate-wrapper">
            <div class="preview-container">
                <div :style="previews.div">
                    <img :src="previews.url" :style="previews.img">
                </div>
            </div>
            <el-button size="small" type="primary" @click="$refs.input.click()">
                选择图片
                <input
                        ref="input"
                        accept="image/png, image/jpeg, image/gif, image/jpg"
                        style="display: none"
                        type="file"
                        @change="chooseImage"
                >
            </el-button>
            <el-button :loading="loading" size="small" type="primary" @click="upload">上 传</el-button>
            <el-button plain size="small" @click="!loading&&clear()">取 消</el-button>
        </div>
    </el-row>
</template>

<script>
    import {VueCropper} from 'vue-cropper'
    import {elError, elSuccess} from "@/utils/message"
    import {autoCompleteUrl, upload} from "@/utils/file"
    import {updateAvatar} from "@/api/system/user"

    export default {
        name: "Avatar",
        components: {VueCropper},
        data() {
            return {
                loading: false,
                previews: {},
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

            upload() {
                if (!this.img) return elError('请先上传图片')
                if (this.loading) return
                this.loading = true
                this.$refs.cropper.getCropBlob(data => {
                    upload(new Blob([data]), this.name)
                        .then(key => updateAvatar(key))
                        .then(({key, msg}) => {
                            this.$store.commit('user/avatar', autoCompleteUrl(key))
                            this.$store.dispatch('user/refresh')
                            elSuccess(msg)
                        })
                        .finally(() => this.clear())
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
            }
        },
        beforeDestroy() {
            this.clear()
        }
    }
</script>

<style lang="scss">
    .avatar-cropper {
        .img-wrapper {
            height: 360px;
            width: 640px;
            display: inline-block;
            border: 1px solid #ebebeb;
        }

        .operate-wrapper {
            padding: 0 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;

            .preview-container {
                border: 1px solid #ebebeb;
                height: 200px;
                width: 200px;
                overflow: hidden;
                border-radius: 50%;
                background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAAA3NCSVQICAjb4U/gAAAABlBMVEXMzMz////TjRV2AAAACXBIWXMAAArrAAAK6wGCiw1aAAAAHHRFWHRTb2Z0d2FyZQBBZG9iZSBGaXJld29ya3MgQ1M26LyyjAAAABFJREFUCJlj+M/AgBVhF/0PAH6/D/HkDxOGAAAAAElFTkSuQmCC");
            }

            .el-button {
                margin-left: 0;
                margin-top: 20px;
                width: 100%;
            }
        }
    }
</style>
