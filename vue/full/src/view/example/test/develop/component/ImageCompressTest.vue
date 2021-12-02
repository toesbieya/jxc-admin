<template>
    <div>
        <div class="tip-row">
            测试纯前端jpg图片压缩效率。
            <a href="https://github.com/LI-NA/mozjpeg.js" target="_blank">使用mozjpeg</a>
            <div class="container">
                <div v-show="!jpg.input.src" class="file-select">
                    <input ref="jpg" type="file" accept="image/jpeg" @change="e => select(e,'jpg')">
                    <el-button type="primary" @click="() => $refs.jpg.click()">选择图片</el-button>
                </div>
                <div v-show="jpg.input.src" class="image-container">
                    <p>原始图片：{{ jpg.name }} ({{ jpg.input.size|number2StorageUnit }})</p>
                    <img :src="jpg.input.src">
                </div>
                <div v-show="jpg.output.src" class="image-container">
                    <p>
                        压缩后大小：({{ jpg.output.size|number2StorageUnit }})，
                        压缩率：{{ jpg.compress }}%，
                        总耗时：{{ jpg.totalCost }}毫秒，
                        计算耗时：{{ jpg.calcCost }}毫秒
                    </p>
                    <img :src="jpg.output.src">
                </div>
                <el-row type="flex">
                    <span>压缩图片品质：{{ jpg.quality }}</span>
                    <el-slider v-model="jpg.quality" :disabled="jpg.loading" :min="0" :max="100" style="flex: 1"/>
                </el-row>
                <el-row type="flex" justify="center">
                    <el-button :loading="jpg.loading" type="primary" @click="() => compress('jpg')">压缩</el-button>
                    <el-button plain @click="() => cancel('jpg')">重置</el-button>
                </el-row>
            </div>
        </div>

        <div class="tip-row">
            测试纯前端png图片压缩效率。
            <a href="https://github.com/psych0der/pngquantjs" target="_blank">使用pngquant</a>
            <div class="container">
                <div v-show="!png.input.src" class="file-select">
                    <input ref="png" type="file" accept="image/x-png" @change="e => select(e,'png')">
                    <el-button type="primary" @click="() => $refs.png.click()">选择图片</el-button>
                </div>
                <div v-show="png.input.src" class="image-container">
                    <p>原始图片：{{ png.name }} ({{ png.input.size|number2StorageUnit }})</p>
                    <img :src="png.input.src">
                </div>
                <div v-show="png.output.src" class="image-container">
                    <p>
                        压缩后大小：({{ png.output.size|number2StorageUnit }})，
                        压缩率：{{ png.compress }}%，
                        总耗时：{{ png.totalCost }}毫秒，
                        计算耗时：{{ png.calcCost }}毫秒
                    </p>
                    <img :src="png.output.src">
                </div>
                <el-row type="flex">
                    <span>压缩图片品质：{{ png.quality }}</span>
                    <el-slider v-model="png.quality" range :disabled="png.loading" :min="0" :max="100" style="flex: 1"/>
                </el-row>
                <el-row type="flex">
                    <span>压缩速度：{{ png.speed }}</span>
                    <el-slider v-model="png.speed" :disabled="png.loading" :min="0" :max="10" style="flex: 1"/>
                </el-row>
                <el-row type="flex" justify="center">
                    <el-button :loading="png.loading" type="primary" @click="() => compress('png')">压缩</el-button>
                    <el-button plain @click="() => cancel('png')">重置</el-button>
                </el-row>
            </div>
        </div>
    </div>
</template>

<script>
import {elError, elSuccess} from "@/util/message"
import {sub, mul, div} from "@/util/math"

const commonData = () => ({
    loading: false,
    name: '',
    compress: 0,
    totalCost: 0,
    calcCost: 0,
    input: {
        size: 0,
        src: '',
        file: null
    },
    output: {
        size: 0,
        src: ''
    }
})

export default {
    name: "ImageCompressTest",
    data() {
        return {
            jpg: {
                ...commonData(),
                quality: 75
            },
            png: {
                ...commonData(),
                quality: [75, 85],
                speed: 4
            }
        }
    },
    methods: {
        select(e, type) {
            const file = e.target.files[0]
            const reg = new RegExp(`.(${type})$`, 'i')
            if (!reg.test(file.name)) {
                elError(`请选择${type}图片`)
                return false
            }
            this[type].name = file.name
            this[type].input.file = file
            this[type].input.size = file.size
            this[type].input.src = window.URL.createObjectURL(file)
        },

        cancel(type) {
            if (this[type].loading) return
            window.URL.revokeObjectURL(this[type].input.src)
            window.URL.revokeObjectURL(this[type].output.src)
            this[type].input.src = ''
            this[type].input.file = null
            this[type].output.src = ''
            this[type].output.file = null
            this[type].compress = 0
            this[type].totalCost = 0
            this[type].calcCost = 0
        },

        compress(type) {
            if (this[type].loading) return
            if (!this[type].input.file) return elError(`请选择一张${type}图片`)
            if (this[type].output.src) return elError('请先清除已压缩的图片')
            const start = window.performance.now()
            this[type].loading = true
            const reader = new FileReader()
            reader.onload = () => {
                const buf = new Uint8Array(reader.result)
                this['compress' + type.toUpperCase()](buf, start)
            }
            reader.readAsArrayBuffer(this[type].input.file)
        },

        compressJPG(uint8Array, start) {
            import('@/plugin/imageCompress/cjpeg')
                .then(_ => _.default(uint8Array, this.jpg.quality))
                .then(({data, time}) => this.compressSuccess({type: 'jpg', start, data, time}))
                .finally(() => this.jpg.loading = false)
        },

        compressPNG(uint8Array, start) {
            import('@/plugin/imageCompress/pngquant')
                .then(_ => _.default(uint8Array, this.png.quality, this.png.speed))
                .then(({data, time}) => this.compressSuccess({type: 'png', start, data, time}))
                .finally(() => this.png.loading = false)
        },

        compressSuccess({type, start, data, time}) {
            this[type].totalCost = (window.performance.now() - start).toFixed(2)
            this[type].calcCost = time.toFixed(2)
            this[type].output.size = data.byteLength
            this[type].output.src = window.URL.createObjectURL(new Blob([data]))
            this[type].compress = mul(sub(1, div(this[type].output.size, this[type].input.size)), 100).toFixed(2)
            elSuccess('压缩成功')
        }
    }
}
</script>

<style lang="scss" scoped>
@import "~@/style/var";

.container {
    max-width: 1200px;
    text-align: center;

    .file-select {
        width: 400px;
        height: 300px;
        margin: 40px auto;
        border: 3px dashed #ccc;
        display: flex;
        justify-content: center;
        align-items: center;

        input {
            display: none;
        }
    }

    .image-container {
        margin: 40px auto;
        text-align: center;

        p {
            color: $--color-text-regular;
            margin-top: 0;
            margin-bottom: 1rem;
        }

        img {
            padding: .25rem;
            background-color: #ffffff;
            border: 1px solid #dee2e6;
            border-radius: .25rem;
            max-width: 100%;
            height: auto;
        }
    }

    .el-slider {
        margin-left: 20px;
    }
}
</style>
