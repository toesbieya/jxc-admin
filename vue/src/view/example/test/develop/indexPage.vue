<template>
    <div>
        <api-speed-test/>
        <export-excel-test/>
        <image-compress-test/>
        <websocket-test/>
    </div>
</template>

<script>
import ApiSpeedTest from "./component/ApiSpeedTest"
import ExportExcelTest from "./component/ExportExcelTest"
import ImageCompressTest from "./component/ImageCompressTest"
import WebsocketTest from "./component/WebsocketTest"

export default {
    name: "testPage",
    components: {ApiSpeedTest, ExportExcelTest, ImageCompressTest, WebsocketTest}
}
</script>
