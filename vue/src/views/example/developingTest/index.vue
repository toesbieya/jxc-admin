<template>
    <div>
        <api-speed-test/>

        <export-excel-test/>

        <image-compress-test/>
    </div>
</template>

<script>
    import ApiSpeedTest from "./components/ApiSpeedTest"
    import ExportExcelTest from "./components/ExportExcelTest"
    import ImageCompressTest from "./components/ImageCompressTest"

    export default {
        name: "testPage",
        components: {ApiSpeedTest, ExportExcelTest, ImageCompressTest}
    }
</script>
